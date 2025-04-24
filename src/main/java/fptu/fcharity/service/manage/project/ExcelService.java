package fptu.fcharity.service.manage.project;

import fptu.fcharity.entity.*;

import fptu.fcharity.entity.SpendingItem; // Assuming entity location

import fptu.fcharity.repository.manage.project.ProjectRepository;
import fptu.fcharity.repository.manage.project.SpendingDetailRepository;
import fptu.fcharity.repository.manage.project.SpendingItemRepository;
import fptu.fcharity.repository.manage.project.SpendingPlanRepository;
import fptu.fcharity.response.project.SpendingPlanReaderResponse;
import fptu.fcharity.utils.constants.request.RequestSupportType;
import fptu.fcharity.utils.exception.ApiRequestException;
import lombok.NonNull;
import org.antlr.v4.runtime.misc.NotNull;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExcelService {

    private static final Logger logger = LoggerFactory.getLogger(ExcelService.class);

    // --- Constants for Spending Items Table ---
    private static final int ITEM_COL_INDEX_ITEM_NAME = 0;
    private static final int ITEM_COL_INDEX_ESTIMATED_COST = 1;
    private static final int ITEM_COL_INDEX_NOTE = 2;
    private static final int NUM_ITEM_DATA_COLUMNS = 3;
    private static final String ITEM_HEADER_ITEM_NAME = "Item Name *";
    private static final String ITEM_HEADER_ESTIMATED_COST = "Estimated Cost *";
    private static final String ITEM_HEADER_NOTE = "Note";

    // --- Constants for Info/Plan Input Section ---
    private static final int INPUT_COL_LABEL = 0; // Column for labels
    private static final int INPUT_COL_VALUE = 1; // Column for values/input cells
    private static final String PLAN_NAME_LABEL = "Plan Name *:"; // Exact label text
    private static final String PLAN_DESC_LABEL = "Description:";  // Exact label text
    private static final String MAX_EXTRA_COST_LABEL = "Max Extra Cost:";

    private static final String SHEET_NAME = "Spending plan details"; // Sheet name
    private static final int NUM_EXTRA_EMPTY_ROWS = 30; // Số dòng trống LUÔN thêm vào


    private final ProjectRepository projectRepository; // Inject if using projectId
    private final SpendingItemRepository spendingItemRepository;
    private final SpendingPlanRepository spendingPlanRepository;
    private final SpendingDetailRepository spendingDetailRepository;

    public ExcelService(ProjectRepository projectRepository,
                        SpendingItemRepository spendingItemRepository,
                        SpendingPlanRepository spendingPlanRepository,
                        SpendingDetailRepository spendingDetailRepository) {
        this.projectRepository = projectRepository;
        this.spendingItemRepository = spendingItemRepository;
        this.spendingPlanRepository = spendingPlanRepository;
        this.spendingDetailRepository = spendingDetailRepository;
    }

    /**
     * Generates an Excel template for Spending Plan and items.
     * If a plan exists for the project, it populates the template with existing data.
     * Otherwise, it provides a blank template for creation.
     *
     * @param projectId The Project ID.
     * @return ByteArrayInputStream containing the Excel template data.
     * @throws IOException If an error occurs during workbook creation.
     * @throws IllegalArgumentException If the project is not found.
     */
    public ByteArrayInputStream generateSpendingPlanTemplate(@NonNull UUID projectId) throws IOException {

        // 1. Fetch Project
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found with ID: " + projectId));

        // 2. Check for Existing Plan and Fetch Data
        SpendingPlan existingPlan = spendingPlanRepository.findByProjectId(projectId); // Or findByProject...
        List<SpendingItem> existingItems = new ArrayList<>();
        String planName = "";
        String planDescription = "";

        if (existingPlan != null) {
            logger.info("Found existing Spending Plan (ID: {}) for Project ID: {}. Populating template.", existingPlan.getId(), projectId);
            planName = existingPlan.getPlanName() != null ? existingPlan.getPlanName() : "";
            planDescription = existingPlan.getDescription() != null ? existingPlan.getDescription() : "";
            existingItems = spendingItemRepository.findBySpendingPlanId(existingPlan.getId());
            if (existingItems == null) { // Ensure list is not null
                existingItems = new ArrayList<>();
            }
            // Sắp xếp item cũ (tùy chọn, ví dụ theo tên)
            existingItems.sort(Comparator.comparing(SpendingItem::getItemName, Comparator.nullsLast(String::compareToIgnoreCase)));
        } else {
            logger.info("No existing Spending Plan found for Project ID: {}. Generating blank template.", projectId);
        }

        // --- Calculate Max Extra Cost (Giữ nguyên logic nếu cần) ---
        // ... (logic tính maxExtraCostPercentage dựa trên project.getRequest()) ...
        // String maxExtraCostPercentage = ...; // Tính toán như cũ
        String maxExtraCostPercentage = "10%"; // Giá trị mặc định ví dụ

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet(SHEET_NAME);

            // --- Create Styles (Giữ nguyên) ---
            Font boldFont = workbook.createFont(); boldFont.setBold(true);
            CellStyle lockedLabelStyle = createCellStyle(workbook, true, boldFont, BorderStyle.THIN, VerticalAlignment.CENTER, false, null, null);
            CellStyle lockedValueStyle = createCellStyle(workbook, true, null, BorderStyle.THIN, VerticalAlignment.CENTER, false, null, null);
            CellStyle unlockedInputStyle = createCellStyle(workbook, false, null, BorderStyle.THIN, VerticalAlignment.TOP, true, null, null); // Wrap text
            CellStyle itemHeaderStyle = createCellStyle(workbook, true, boldFont, BorderStyle.THIN, VerticalAlignment.CENTER, true, IndexedColors.GREY_25_PERCENT.getIndex(), FillPatternType.SOLID_FOREGROUND);
            itemHeaderStyle.setAlignment(HorizontalAlignment.CENTER);
            CellStyle unlockedItemStyle = createCellStyle(workbook, false, null, BorderStyle.THIN, VerticalAlignment.TOP, false, null, null); // No wrap text
            DataFormat format = workbook.createDataFormat();
            CellStyle costItemStyle = createCellStyle(workbook, false, null, BorderStyle.THIN, VerticalAlignment.TOP, false, null, null); // No wrap text
            costItemStyle.setDataFormat(format.getFormat("#,##0.00"));
            Font instructionFont = workbook.createFont(); instructionFont.setItalic(true); instructionFont.setColor(IndexedColors.GREY_50_PERCENT.getIndex());
            CellStyle instructionStyle = createCellStyle(workbook, true, instructionFont, BorderStyle.NONE, VerticalAlignment.TOP, true, null, null);

            int currentRowIndex = 0;

            // --- Section 0: Instructions (Giữ nguyên) ---
            int instructionStartRow = currentRowIndex;
            Row instructionRow = sheet.createRow(instructionStartRow);
            instructionRow.setHeightInPoints(4 * sheet.getDefaultRowHeightInPoints());
            int estimatedFirstItemDataRowIndex = instructionStartRow + 3 + 1 + 2 + 1 + 2 + 1 + 1;
            Cell instructionCell = createCell(instructionRow, 0,
                    "Instructions:\n"
                            + "1. DO NOT MODIFY cells with gray backgrounds or labels.\n"
                            + "2. Enter/Modify the Plan Name (required) and Description.\n"
                            + "3. Enter/Modify details for spending items in the rows below, starting from row " + (estimatedFirstItemDataRowIndex + 1) + ".\n"
                            + "4. To add more items, insert new rows below the last item.", // Hướng dẫn đơn giản hơn
                    instructionStyle);
            sheet.addMergedRegion(new CellRangeAddress(instructionStartRow, instructionStartRow + 2, 0, NUM_ITEM_DATA_COLUMNS - 1));
            currentRowIndex = instructionStartRow + 3; currentRowIndex++;


            // --- Section 1: Project Info (Giữ nguyên) ---
            createLabelValueRow(sheet, currentRowIndex++, "Project Name:", project.getProjectName(), lockedLabelStyle, lockedValueStyle);
            createLabelValueRow(sheet, currentRowIndex++, "Max Extra Cost:", maxExtraCostPercentage, lockedLabelStyle, lockedValueStyle);
            currentRowIndex++;


            // --- Section 2: Spending Plan Input (Điền dữ liệu nếu có) ---
            Row planNameInputRow = sheet.createRow(currentRowIndex++);
            createCell(planNameInputRow, INPUT_COL_LABEL, PLAN_NAME_LABEL, lockedLabelStyle);
            // *** Điền planName vào ô giá trị nếu có ***
            createCell(planNameInputRow, INPUT_COL_VALUE, planName, unlockedInputStyle);

            Row planDescInputRow = sheet.createRow(currentRowIndex++);
            planDescInputRow.setHeightInPoints(3 * sheet.getDefaultRowHeightInPoints());
            createCell(planDescInputRow, INPUT_COL_LABEL, PLAN_DESC_LABEL, lockedLabelStyle);
            // *** Điền planDescription vào ô giá trị nếu có ***
            createCell(planDescInputRow, INPUT_COL_VALUE, planDescription, unlockedInputStyle);
            currentRowIndex++;


            // --- Section 3: Spending Items Table ---
            int itemTableHeaderRowIndex = currentRowIndex++;
            int firstItemDataRowIndex = currentRowIndex;

            // Row X: Item Table Header (Giữ nguyên)
            Row itemHeaderRow = sheet.createRow(itemTableHeaderRowIndex);
            String[] itemHeaders = {ITEM_HEADER_ITEM_NAME, ITEM_HEADER_ESTIMATED_COST, ITEM_HEADER_NOTE};
            for (int i = 0; i < itemHeaders.length; i++) {
                createCell(itemHeaderRow, i, itemHeaders[i], itemHeaderStyle); // Dùng helper mới
            }

            // --- Section 3a: Populate Existing Items ---
            for (SpendingItem item : existingItems) {
                Row dataRow = sheet.createRow(currentRowIndex++);
                createCell(dataRow, ITEM_COL_INDEX_ITEM_NAME, item.getItemName(), unlockedItemStyle);
                // Chuyển EstimatedCost (có thể là BigDecimal) sang double
                double cost = (item.getEstimatedCost() != null) ? item.getEstimatedCost().doubleValue() : 0.0;
                createCell(dataRow, ITEM_COL_INDEX_ESTIMATED_COST, cost, costItemStyle);
                createCell(dataRow, ITEM_COL_INDEX_NOTE, item.getNote(), unlockedItemStyle);
            }

            // --- Section 3b: Add Extra Empty Rows (Luôn thêm) ---
            for (int i = 0; i < NUM_EXTRA_EMPTY_ROWS; i++) {
                Row dataRow = sheet.createRow(currentRowIndex++);
                createCell(dataRow, ITEM_COL_INDEX_ITEM_NAME, "", unlockedItemStyle);
                createCell(dataRow, ITEM_COL_INDEX_ESTIMATED_COST, 0.0, costItemStyle);
                createCell(dataRow, ITEM_COL_INDEX_NOTE, "", unlockedItemStyle);
            }


            // --- Auto-size / Set Column Widths (Giữ nguyên) ---
            sheet.autoSizeColumn(INPUT_COL_LABEL);
            sheet.setColumnWidth(INPUT_COL_VALUE, 60 * 256);
            sheet.setColumnWidth(ITEM_COL_INDEX_ITEM_NAME, 40 * 256);
            sheet.setColumnWidth(ITEM_COL_INDEX_ESTIMATED_COST, 20 * 256);
            sheet.setColumnWidth(ITEM_COL_INDEX_NOTE, 45 * 256);


            // --- Protect Sheet (Giữ nguyên) ---
            sheet.protectSheet("");


            // --- Write to output stream (Giữ nguyên) ---
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (Exception e) {
            logger.error("Error generating Spending Plan template for Project ID {}: {}", projectId, e.getMessage(), e);
            throw new IOException("Failed to generate spending plan template: " + e.getMessage(), e);
        }
    }

    // --- Helper methods (Giữ nguyên hoặc tạo mới nếu chưa có) ---

    private void createLabelValueRow(Sheet sheet, int rowIndex, String label, String value, CellStyle labelStyle, CellStyle valueStyle) {
        Row row = sheet.createRow(rowIndex);
        createCell(row, INPUT_COL_LABEL, label, labelStyle);
        createCell(row, INPUT_COL_VALUE, value, valueStyle);
    }
    private CellStyle createCellStyle(Workbook workbook, boolean locked, Font font, BorderStyle border, VerticalAlignment valign, boolean wrapText, Short fgColor, FillPatternType fpType) {
        CellStyle style = workbook.createCellStyle();
        style.setLocked(locked);
        if (font != null) style.setFont(font);
        if (border != null) { /* set borders */ style.setBorderBottom(border); style.setBorderTop(border); style.setBorderLeft(border); style.setBorderRight(border); }
        if (valign != null) style.setVerticalAlignment(valign);
        style.setWrapText(wrapText);
        if (fgColor != null && fpType != null) { /* set background */ style.setFillForegroundColor(fgColor); style.setFillPattern(fpType); }
        return style;
    }
    /**
     * Parses the uploaded Excel file based on the template generated by
     * generateNewSpendingPlanTemplate. Extracts Plan details, Item list, and Max Extra Cost %.
     * Only includes items where at least the Item Name has been provided by the user.
     * The returned SpendingPlan will NOT have the Project field set.
     *
     * @param file Uploaded MultipartFile.
     * @return SpendingPlanReaderResponse containing parsed data and errors.
     * @throws IOException If the file cannot be read.
     * @throws IllegalArgumentException If basic file validation fails.
     */
    public SpendingPlanReaderResponse parseNewSpendingPlanExcel(MultipartFile file) throws IOException {
        // 1. Initial Validations & Setup
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file is empty or null.");
        }
        String contentType = file.getContentType();
        if (!Objects.equals(contentType, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") &&
                !Objects.equals(contentType, "application/vnd.ms-excel")) {
            logger.warn("Invalid file type uploaded: {}", contentType);
            throw new IllegalArgumentException("Invalid file type. Please upload an Excel file (.xlsx or .xls).");
        }

        SpendingPlanReaderResponse response = new SpendingPlanReaderResponse();
        InputStream inputStream = file.getInputStream();
        DataFormatter dataFormatter = new DataFormatter();
        boolean foundPlanName = false;
        boolean foundPlanDesc = false;
        boolean foundMaxExtraCost = false;
        boolean foundItemHeader = false;
        int itemTableHeaderRowIndex = -1;
        BigDecimal parsedMaxExtraCostMultiplier = null;
        BigDecimal parsedMaxExtraCostPercentValue = null;

        // 2. Process the Workbook
        try (Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheet = workbook.getSheet(SHEET_NAME);
            if (sheet == null) {
                response.getErrors().add("Sheet '" + SHEET_NAME + "' not found in the uploaded file.");
                return response;
            }

            Iterator<Row> rowIterator = sheet.iterator();
            int physicalRowNum = 0;

            // 3. Iterate Through Rows
            while (rowIterator.hasNext()) {
                Row currentRow = rowIterator.next();
                physicalRowNum++;
                int currentRowIndex = currentRow.getRowNum();

                // --- 3a. Find Plan Name, Description, and Max Extra Cost ---
                if (!foundPlanName || !foundPlanDesc || !foundMaxExtraCost) {
                    Cell labelCell = currentRow.getCell(INPUT_COL_LABEL, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    if (labelCell != null) {
                        String label = getStringCellValue(labelCell, dataFormatter);

                        if (PLAN_NAME_LABEL.equalsIgnoreCase(label)) {
                            Cell valueCell = currentRow.getCell(INPUT_COL_VALUE, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                            String planName = getStringCellValue(valueCell, dataFormatter);
                            if (planName == null || planName.trim().isEmpty()) {
                                response.getErrors().add("Plan Name (Row " + physicalRowNum + ") is required but empty.");
                            } else {
                                response.getSpendingPlan().setPlanName(planName.trim());
                            }
                            foundPlanName = true;
                            continue;
                        } else if (PLAN_DESC_LABEL.equalsIgnoreCase(label)) {
                            Cell valueCell = currentRow.getCell(INPUT_COL_VALUE, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                            String planDesc = getStringCellValue(valueCell, dataFormatter);
                            response.getSpendingPlan().setDescription(planDesc != null ? planDesc.trim() : null);
                            foundPlanDesc = true;
                            continue;
                        } else if (MAX_EXTRA_COST_LABEL.equalsIgnoreCase(label)) {
                            Cell valueCell = currentRow.getCell(INPUT_COL_VALUE, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                            String percentageStr = getStringCellValue(valueCell, dataFormatter);
                            if (percentageStr == null || percentageStr.trim().isEmpty()) {
                                response.getErrors().add("Max Extra Cost value (Row " + physicalRowNum + ") is missing.");
                            } else {
                                try {
                                    String numericPart = percentageStr.replace("%", "").trim();
                                    BigDecimal percentValue = new BigDecimal(numericPart);
                                    if (percentValue.compareTo(BigDecimal.ZERO) < 0) {
                                        response.getErrors().add("Max Extra Cost percentage (Row " + physicalRowNum + ") cannot be negative.");
                                    } else {
                                        parsedMaxExtraCostPercentValue = percentValue.setScale(2, RoundingMode.HALF_UP);
                                        parsedMaxExtraCostMultiplier = percentValue.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
                                    }
                                } catch (NumberFormatException e) {
                                    response.getErrors().add("Max Extra Cost value '" + percentageStr + "' (Row " + physicalRowNum + ") is not a valid percentage format.");
                                }
                            }
                            foundMaxExtraCost = true;
                            continue;
                        }
                    }
                }

                // --- 3b. Find Item Table Header ---
                if (!foundItemHeader) {
                    if (isValidItemHeader(currentRow, dataFormatter)) {
                        foundItemHeader = true;
                        itemTableHeaderRowIndex = currentRowIndex;
                        continue;
                    }
                }

                // --- 3c. Parse Spending Items ---
                if (foundItemHeader && currentRowIndex > itemTableHeaderRowIndex) {

                    // *** MODIFICATION START: Check for content before processing ***
                    // Get potential item name first
                    Cell nameCheckCell = currentRow.getCell(ITEM_COL_INDEX_ITEM_NAME, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    String potentialItemName = getStringCellValue(nameCheckCell, dataFormatter);

                    // If Item Name is blank/null, consider the row empty for import purposes, even if 0.0 cost or note exists
                    if (potentialItemName == null || potentialItemName.trim().isEmpty()) {
                        // Optionally check if other cells *do* have content, which might indicate an incomplete entry
                        Cell costCheckCell = currentRow.getCell(ITEM_COL_INDEX_ESTIMATED_COST, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                        Cell noteCheckCell = currentRow.getCell(ITEM_COL_INDEX_NOTE, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                        BigDecimal potentialCost = getNumericCellValue(costCheckCell, dataFormatter);
                        String potentialNote = getStringCellValue(noteCheckCell, dataFormatter);
                        if((potentialCost != null && potentialCost.compareTo(BigDecimal.ZERO) != 0) || (potentialNote != null && !potentialNote.isEmpty())) {
                            // Only log or add error if there *was* other data but name was missing
                            logger.warn("Skipping item row {} because Item Name is missing, although other data might be present.", physicalRowNum);
                            // Optionally add a less severe warning to the response if needed:
                            // response.getErrors().add("Warning: Item Row " + physicalRowNum + " skipped due to missing Item Name.");
                        } else {
                            // Row is genuinely empty or only has default cost, silently skip
                            // logger.debug("Skipping effectively empty item row {}", physicalRowNum);
                        }
                        continue; // Skip this row
                    }
                    // *** MODIFICATION END ***


                    // Proceed with parsing if Item Name was present
                    SpendingItem item = new SpendingItem();
                    item.setSpendingPlan(response.getSpendingPlan());
                    boolean rowHasError = false;
                    String errorPrefix = "Item Row " + physicalRowNum + ": ";

                    // Set the already parsed Item Name
                    item.setItemName(potentialItemName.trim()); // We know it's not null/empty here

                    // Parse Estimated Cost
                    Cell costCell = currentRow.getCell(ITEM_COL_INDEX_ESTIMATED_COST, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    BigDecimal estimatedCost = getNumericCellValue(costCell, dataFormatter);
                    if (estimatedCost == null) {
                        // Since Item Name exists, missing cost IS an error now
                        response.getErrors().add(errorPrefix + "Estimated Cost is missing or not a valid number.");
                        rowHasError = true;
                    } else if (estimatedCost.compareTo(BigDecimal.ZERO) < 0) {
                        response.getErrors().add(errorPrefix + "Estimated Cost cannot be negative (Value: " + estimatedCost.toPlainString() + ").");
                        rowHasError = true;
                    }
                    item.setEstimatedCost(estimatedCost != null ? estimatedCost.setScale(2, RoundingMode.HALF_UP) : null);

                    // Parse Note (Optional)
                    Cell noteCell = currentRow.getCell(ITEM_COL_INDEX_NOTE, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    String note = getStringCellValue(noteCell, dataFormatter);
                    item.setNote(note != null ? note.trim() : null);

                    // Add item only if no errors occurred *for this specific row*
                    if (!rowHasError) {
                        response.getSpendingItems().add(item);
                    }
                }
            } // End while loop

            // 4. Final Validation Checks (Keep as before)
            if (!foundPlanName) response.getErrors().add("Could not find the 'Plan Name *:' label in the file.");
            else if (response.getSpendingPlan().getPlanName() == null) {/* Error added previously */}
            if (!foundMaxExtraCost) response.getErrors().add("Could not find the 'Max Extra Cost:' label in the file.");
            if (!foundItemHeader) response.getErrors().add("Could not find the Spending Items table header.");
            // Modify this check slightly: error if header found but NO items added AND no previous errors prevented adding items
            if (foundItemHeader && response.getSpendingItems().isEmpty() && !response.hasErrors()) {
                response.getErrors().add("No valid spending items with required information were entered in the table.");
            }

        } catch (IOException e) {
            logger.error("IOException during Excel file processing.", e);
            response.getErrors().add("Error reading the Excel file: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error during Excel file processing.", e);
            response.getErrors().add("An unexpected error occurred: " + e.getMessage());
        }

        // 5. Log and Return (Keep as before)
        if (response.hasErrors()) {
            logger.error("Validation errors occurred during Excel parsing: {}", response.getErrors());
        } else {
            response.getSpendingPlan().setMaxExtraCostPercentage(parsedMaxExtraCostPercentValue);
            calculateAndSetEstimatedTotal(response, parsedMaxExtraCostMultiplier);
            logger.info("Successfully parsed Spending Plan ('{}'), Max Extra Cost {}%, with {} items. Calculated Total: {}",
                    response.getSpendingPlan().getPlanName(),
                    response.getSpendingPlan().getMaxExtraCostPercentage(),
                    response.getSpendingItems().size(),
                    response.getSpendingPlan().getEstimatedTotalCost());
        }

        return response;
    }

    // --- Helper Method to Calculate Total Cost (Keep as before) ---
    private void calculateAndSetEstimatedTotal(SpendingPlanReaderResponse response, BigDecimal maxExtraCostMultiplier) {
        // ... (Calculation logic remains the same)
        BigDecimal initialTotal = BigDecimal.ZERO;
        if (response.getSpendingItems() != null && !response.getSpendingItems().isEmpty()) {
            initialTotal = response.getSpendingItems().stream()
                    .map(SpendingItem::getEstimatedCost)
                    .filter(Objects::nonNull) // Ensure cost is not null
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        BigDecimal finalTotal = initialTotal; // Start with the sum of items

        // Add extra cost only if multiplier is valid and positive
        if (maxExtraCostMultiplier != null && maxExtraCostMultiplier.compareTo(BigDecimal.ZERO) > 0) {
            // Calculate extra cost with sufficient precision before rounding final result
            BigDecimal extraCost = initialTotal.multiply(maxExtraCostMultiplier);
            finalTotal = initialTotal.add(extraCost);
            logger.info("Calculated initial total: {}, Extra cost factor: {}, Added extra cost: {}", initialTotal, maxExtraCostMultiplier, extraCost);
        } else {
            logger.info("Calculated initial total: {}. No positive extra cost factor found or applied.", initialTotal);
        }

        // Set the final calculated total cost on the plan
        response.getSpendingPlan().setEstimatedTotalCost(finalTotal.setScale(2, RoundingMode.HALF_UP));
        logger.info("Final Estimated Total Cost set to: {}", response.getSpendingPlan().getEstimatedTotalCost());
    }

    // --- Helper Methods for Parsing (Make sure these match the previous correct versions) ---

    /** Checks if the row contains the expected Item Table headers. */
    private boolean isValidItemHeader(Row headerRow, DataFormatter formatter) {
        // Renamed from isValidHeader to be specific
        if (headerRow == null) return false;
        Cell cell1 = headerRow.getCell(ITEM_COL_INDEX_ITEM_NAME);
        Cell cell2 = headerRow.getCell(ITEM_COL_INDEX_ESTIMATED_COST);
        Cell cell3 = headerRow.getCell(ITEM_COL_INDEX_NOTE);
        return cell1 != null && ITEM_HEADER_ITEM_NAME.equals(formatter.formatCellValue(cell1).trim()) &&
                cell2 != null && ITEM_HEADER_ESTIMATED_COST.equals(formatter.formatCellValue(cell2).trim()) &&
                cell3 != null && ITEM_HEADER_NOTE.equals(formatter.formatCellValue(cell3).trim());
    }

    /** Gets the string value of a cell safely, trimming whitespace. Returns null for blank/error cells. */
    private String getStringCellValue(Cell cell, DataFormatter formatter) {
        if (cell == null) return null;
        String value = formatter.formatCellValue(cell).trim();
        if (value.isEmpty() || (cell.getCellType() == CellType.ERROR)) {
            if (cell.getCellType() == CellType.ERROR) {
                try { logger.warn("Cell at row {}, col {} contains error: {}", cell.getRowIndex()+1, cell.getColumnIndex()+1, ErrorEval.getText(cell.getErrorCellValue())); } catch (Exception ignored) {}
            }
            return null;
        }
        return value;
    }

    /** Gets the BigDecimal value of a cell safely. Returns null for blank/error/non-numeric cells. */
    private BigDecimal getNumericCellValue(Cell cell, DataFormatter formatter) {
        if (cell == null) return null;
        CellType cellType = cell.getCellType();
        if (cellType == CellType.FORMULA) {
            try {
                cellType = cell.getCachedFormulaResultType();
            } catch (IllegalStateException e) {
                logger.warn("Could not evaluate formula type at row {}, col {}: {}", cell.getRowIndex()+1, cell.getColumnIndex()+1, e.getMessage());
                return null; // Cannot evaluate formula
            }
        }
        switch (cellType) {
            case NUMERIC:
                double doubleValue = cell.getNumericCellValue();
                if (Double.isNaN(doubleValue) || Double.isInfinite(doubleValue)) return null;
                try { return BigDecimal.valueOf(doubleValue); } catch (NumberFormatException e) { return null; }
            case STRING:
                String stringValue = cell.getStringCellValue().trim();
                if (stringValue.isEmpty()) return null;
                try {
                    stringValue = stringValue.replaceAll("[^\\d.,-]", "");
                    if (stringValue.contains(",")) stringValue = stringValue.replace(",", ".");
                    if (stringValue.indexOf('.') != stringValue.lastIndexOf('.')) return null;
                    return new BigDecimal(stringValue);
                } catch (NumberFormatException e) { return null; }
            case BLANK: return null;
            case ERROR: return null;
            default: return null;
        }
    }

    /** Checks if a row is effectively empty based on the item data columns. */
    private boolean isRowEffectivelyEmpty(Row row) {
        if (row == null) return true;
        DataFormatter formatter = new DataFormatter();
        for (int colIndex : new int[]{ITEM_COL_INDEX_ITEM_NAME, ITEM_COL_INDEX_ESTIMATED_COST, ITEM_COL_INDEX_NOTE}) {
            Cell cell = row.getCell(colIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                if (!formatter.formatCellValue(cell).trim().isEmpty()) return false;
            }
        }
        return true;
    }


    /**
     *
     *
     *
     *
     *
     *
     * */
    // --- Constants for Spending Detail Template & Import ---
    private static final String DETAIL_SHEET_NAME = "Spending Details";
    private static final String ITEM_LIST_SHEET_NAME = "ItemList"; // Hidden sheet
    private static final int DETAIL_COL_INDEX_ITEM_SELECT = 0;
    private static final int DETAIL_COL_INDEX_AMOUNT = 1;
    private static final int DETAIL_COL_INDEX_TRANS_TIME = 2;
    private static final int DETAIL_COL_INDEX_DESCRIPTION = 3;
    private static final int DETAIL_COL_INDEX_PROOF_URL = 4;
    private static final int NUM_DETAIL_DATA_COLUMNS = 5;

    private static final String DETAIL_HEADER_ITEM_SELECT = "Spending Item *";
    private static final String DETAIL_HEADER_AMOUNT = "Amount *";
    private static final String DETAIL_HEADER_TRANS_TIME = "Transaction Time (YYYY-MM-DD)"; // Specify format
    private static final String DETAIL_HEADER_DESCRIPTION = "Description";
    private static final String DETAIL_HEADER_PROOF_URL = "Proof Image URL";

    // Assuming format for parsing later
    public static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd";
    // Giả định header ở dòng index 9 (0-based) dựa trên logic generate
    // Instructions(5) + Blank(1) + Project(1) + Plan(1) + Blank(1) = Header row index 9
    private static final int DETAIL_HEADER_ROW_INDEX = 9;
    private static final int FIRST_DATA_ROW_INDEX = DETAIL_HEADER_ROW_INDEX + 1; // Dòng dữ liệu đầu tiên

    // Format date dùng trong Excel và để parse nếu người dùng nhập tay
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT_PATTERN);

    // Cụm từ cần loại bỏ (giống generate)
    private static final String PHRASE_TO_EXCLUDE = "extra funds";

    private static final String ALLOWED_EXTENSION = "xlsx"; // Chỉ cho phép file .xlsx


    // --- NEW Constants ---
    private static final String EXCLUDED_PHRASE_IN_DESCRIPTION = "extra funds for project";

    public ByteArrayInputStream generateSpendingDetailTemplate(@NonNull UUID projectId) throws IOException {

        // 1. Fetch Project and Active Spending Plan (Giữ nguyên)
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found with ID: " + projectId));
        SpendingPlan activePlan = spendingPlanRepository.findByProjectId(projectId); // Hoặc logic tìm plan chính xác
        if (activePlan == null) {
            throw new IllegalArgumentException("No active or relevant Spending Plan found for Project ID: " + projectId);
        }

        // 2. Fetch Spending Items for Dropdown (Giữ nguyên)
        List<SpendingItem> spendingItems = spendingItemRepository.findBySpendingPlanId(activePlan.getId());
        if (spendingItems == null || spendingItems.isEmpty()) {
            throw new IllegalArgumentException("No Spending Items found for the active Spending Plan (ID: " + activePlan.getId() + ").");
        }
        final String phraseToExcludeFromItemName = "extra funds";
        List<String> itemNamesForDropdown = spendingItems.stream()
                .map(SpendingItem::getItemName)
                .filter(Objects::nonNull)
                .filter(name -> !name.trim().toLowerCase().contains(phraseToExcludeFromItemName.toLowerCase()))
                .distinct().sorted().collect(Collectors.toList());
        if (itemNamesForDropdown.isEmpty()) {
            throw new IllegalArgumentException("No spending items available for selection (excluding those named 'extra funds') for Spending Plan (ID: " + activePlan.getId() + ").");
        }

        // 3. Fetch and Filter Existing Spending Details (Giữ nguyên)
        List<SpendingDetail> existingDetails = spendingDetailRepository.findByProjectId(projectId);
        List<SpendingDetail> detailsToInclude = new ArrayList<>();
        final String excludedDescPhraseLower = EXCLUDED_PHRASE_IN_DESCRIPTION.toLowerCase();
        if (existingDetails != null && !existingDetails.isEmpty()) {
            detailsToInclude = existingDetails.stream()
                    .filter(detail -> detail.getDescription() == null ||
                            !detail.getDescription().toLowerCase().contains(excludedDescPhraseLower))
                    .sorted(Comparator.comparing(SpendingDetail::getTransactionTime, Comparator.nullsLast(Comparator.reverseOrder())))
                    .collect(Collectors.toList());
            logger.info("Found {} existing spending details to include in template for project {}", detailsToInclude.size(), projectId);
        } else {
            logger.info("No existing spending details found for project {}", projectId);
        }


        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            // --- 4. Create Styles (Thêm các style "Locked") ---
            CreationHelper createHelper = workbook.getCreationHelper();
            DataFormat dataFormat = workbook.createDataFormat();

            Font boldFont = workbook.createFont(); boldFont.setBold(true);
            Font instructionFont = workbook.createFont(); instructionFont.setItalic(true); instructionFont.setColor(IndexedColors.GREY_50_PERCENT.getIndex());

            // Base Styles
            CellStyle lockedLabelStyle = createCellStyle(workbook, true, boldFont, BorderStyle.THIN, VerticalAlignment.CENTER, false, null, null);
            CellStyle lockedValueStyle = createCellStyle(workbook, true, null, BorderStyle.THIN, VerticalAlignment.CENTER, false, null, null);
            CellStyle instructionStyle = createCellStyle(workbook, true, instructionFont, BorderStyle.NONE, VerticalAlignment.TOP, true, null, null);
            CellStyle detailHeaderStyle = createCellStyle(workbook, true, boldFont, BorderStyle.THIN, VerticalAlignment.CENTER, true, IndexedColors.SEA_GREEN.getIndex(), FillPatternType.SOLID_FOREGROUND);
            detailHeaderStyle.setAlignment(HorizontalAlignment.CENTER);

            // --- UNLOCKED Styles for Data Rows ---
            CellStyle unlockedDetailStyle = createCellStyle(workbook, false, null, BorderStyle.THIN, VerticalAlignment.TOP, false, null, null);
            CellStyle unlockedAmountStyle = createCellStyle(workbook, false, null, BorderStyle.THIN, VerticalAlignment.TOP, false, null, null);
            unlockedAmountStyle.setDataFormat(dataFormat.getFormat("#,##0.00")); // Apply number format
            CellStyle unlockedDateStyle = createCellStyle(workbook, false, null, BorderStyle.THIN, VerticalAlignment.TOP, false, null, null);
            unlockedDateStyle.setDataFormat(dataFormat.getFormat(DATE_FORMAT_PATTERN)); // Apply date format
            CellStyle unlockedInputStyle = createCellStyle(workbook, false, null, BorderStyle.THIN, VerticalAlignment.TOP, true, null, null); // Wrap text
            CellStyle unlockedUrlStyle = createCellStyle(workbook, false, null, BorderStyle.THIN, VerticalAlignment.TOP, false, IndexedColors.LIGHT_GREEN.getIndex(), FillPatternType.SOLID_FOREGROUND); // Light Green bg

            // --- LOCKED Styles for Data Rows (Mirroring Unlocked ones) ---
            CellStyle lockedDetailStyle = createCellStyle(workbook, true, null, BorderStyle.THIN, VerticalAlignment.TOP, false, null, null); // Locked version of unlockedDetailStyle
            CellStyle lockedAmountStyle = createCellStyle(workbook, true, null, BorderStyle.THIN, VerticalAlignment.TOP, false, null, null);
            lockedAmountStyle.setDataFormat(dataFormat.getFormat("#,##0.00")); // Keep number format
            CellStyle lockedDateStyle = createCellStyle(workbook, true, null, BorderStyle.THIN, VerticalAlignment.TOP, false, null, null);
            lockedDateStyle.setDataFormat(dataFormat.getFormat(DATE_FORMAT_PATTERN)); // Keep date format
            CellStyle lockedInputStyleForDesc = createCellStyle(workbook, true, null, BorderStyle.THIN, VerticalAlignment.TOP, true, null, null); // Locked version of unlockedInputStyle
            CellStyle lockedUrlStyle = createCellStyle(workbook, true, null, BorderStyle.THIN, VerticalAlignment.TOP, false, IndexedColors.LIGHT_GREEN.getIndex(), FillPatternType.SOLID_FOREGROUND); // Keep background, but locked


            // 5. Create Main Sheet (Spending Details)
            XSSFSheet detailSheet = workbook.createSheet(DETAIL_SHEET_NAME);
            int currentRowIndex = 0;

            // Section 0: Instructions (Giữ nguyên)
            // ... (code tạo instructions)
            int instructionStartRow = currentRowIndex;
            Row instructionRow = detailSheet.createRow(instructionStartRow);
            instructionRow.setHeightInPoints(5 * detailSheet.getDefaultRowHeightInPoints());
            int estimatedFirstDetailDataRowIndex = instructionStartRow + 5 + 1 + 2 + 1 + 1;
            Cell instructionCell = createCell(instructionRow, 0,
                    "Instructions:\n"
                            + "... 6. Input data in the empty rows starting from row " + (estimatedFirstDetailDataRowIndex + 1) + ". Only data from today can be modified.\n" // Sửa hướng dẫn
                            + "...", instructionStyle);
            detailSheet.addMergedRegion(new CellRangeAddress(instructionStartRow, instructionStartRow + 4, 0, NUM_DETAIL_DATA_COLUMNS - 1));
            currentRowIndex = instructionStartRow + 5; currentRowIndex++;

            // Section 1: Read-Only Info (Giữ nguyên)
            createLabelValueRow(detailSheet, currentRowIndex++, "Project Name:", project.getProjectName(), lockedLabelStyle, lockedValueStyle);
            createLabelValueRow(detailSheet, currentRowIndex++, "Spending Plan:", activePlan.getPlanName(), lockedLabelStyle, lockedValueStyle);
            currentRowIndex++;

            // Section 2: Spending Details Table Header (Giữ nguyên)
            int detailHeaderRowIndex = currentRowIndex++;
            Row headerRow = detailSheet.createRow(detailHeaderRowIndex);
            String[] detailHeaders = {DETAIL_HEADER_ITEM_SELECT, DETAIL_HEADER_AMOUNT, DETAIL_HEADER_TRANS_TIME, DETAIL_HEADER_DESCRIPTION, DETAIL_HEADER_PROOF_URL};
            for (int i = 0; i < detailHeaders.length; i++) {
                createCell(headerRow, i, detailHeaders[i], detailHeaderStyle);
            }
            int firstDataRow = currentRowIndex;

            // --- Section 3a: Populate Rows with Existing Data (Apply Conditional Styles) ---
            LocalDate today = LocalDate.now(ZoneId.systemDefault()); // Lấy ngày hiện tại (hoặc múi giờ cụ thể)

            for (SpendingDetail detail : detailsToInclude) {
                Row dataRow = detailSheet.createRow(currentRowIndex++);

                // Xác định xem dòng này có được sửa không
                LocalDate transactionDate = detail.getTransactionTime() != null ?
                        detail.getTransactionTime().atZone(ZoneId.systemDefault()).toLocalDate() : null; // Chuyển Instant sang LocalDate
                boolean isEditableRow = transactionDate != null && transactionDate.equals(today);

                // Item Name
                String itemName = (detail.getSpendingItem() != null) ? detail.getSpendingItem().getItemName() : "";
                createCell(dataRow, DETAIL_COL_INDEX_ITEM_SELECT, itemName,
                        isEditableRow ? unlockedDetailStyle : lockedDetailStyle); // Style có điều kiện

                // Amount
                double amount = (detail.getAmount() != null) ? detail.getAmount().doubleValue() : 0.0;
                createCell(dataRow, DETAIL_COL_INDEX_AMOUNT, amount,
                        isEditableRow ? unlockedAmountStyle : lockedAmountStyle); // Style có điều kiện

                // Transaction Time
                Date transTime = (detail.getTransactionTime() != null) ? Date.from(detail.getTransactionTime()) : null;
                createCell(dataRow, DETAIL_COL_INDEX_TRANS_TIME, transTime,
                        isEditableRow ? unlockedDateStyle : lockedDateStyle); // Style có điều kiện

                // Description
                createCell(dataRow, DETAIL_COL_INDEX_DESCRIPTION, detail.getDescription(),
                        isEditableRow ? unlockedInputStyle : lockedInputStyleForDesc); // Style có điều kiện

                // Proof Image URL
                createCell(dataRow, DETAIL_COL_INDEX_PROOF_URL, detail.getProofImage(),
                        isEditableRow ? unlockedUrlStyle : lockedUrlStyle); // Style có điều kiện
            }

            // --- Section 3b: Add Extra Empty Rows (Always Unlocked) ---
            for (int i = 0; i < NUM_EXTRA_EMPTY_ROWS; i++) {
                Row dataRow = detailSheet.createRow(currentRowIndex++);
                createCell(dataRow, DETAIL_COL_INDEX_ITEM_SELECT, "", unlockedDetailStyle);
                createCell(dataRow, DETAIL_COL_INDEX_AMOUNT, 0.0, unlockedAmountStyle);
                createCell(dataRow, DETAIL_COL_INDEX_TRANS_TIME, "", unlockedDateStyle);
                createCell(dataRow, DETAIL_COL_INDEX_DESCRIPTION, "", unlockedInputStyle);
                createCell(dataRow, DETAIL_COL_INDEX_PROOF_URL, "", unlockedUrlStyle);
            }
            int lastDataRow = currentRowIndex - 1;


            // 6. Create Hidden Sheet for Item List (Giữ nguyên)
            // ... (code tạo hidden sheet) ...
            Sheet listSheet = workbook.createSheet(ITEM_LIST_SHEET_NAME);
            for (int i = 0; i < itemNamesForDropdown.size(); i++) {
                Row listRow = listSheet.createRow(i);
                createCell(listRow, 0, itemNamesForDropdown.get(i), null);
            }
            Name namedRange = workbook.createName();
            namedRange.setNameName("SpendingItemsList");
            String reference = ITEM_LIST_SHEET_NAME + "!$A$1:$A$" + itemNamesForDropdown.size();
            namedRange.setRefersToFormula(reference);
            workbook.setSheetHidden(workbook.getSheetIndex(listSheet), true);

            // 7. Apply Data Validation (Adjust Range - Giữ nguyên)
            // ... (code áp dụng data validation cho item và date, đảm bảo dùng lastDataRow) ...
            XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper(detailSheet);
            // Item dropdown
            XSSFDataValidationConstraint itemDvConstraint = (XSSFDataValidationConstraint) dvHelper.createFormulaListConstraint("SpendingItemsList");
            CellRangeAddress itemRange = new CellRangeAddress(firstDataRow, lastDataRow, DETAIL_COL_INDEX_ITEM_SELECT, DETAIL_COL_INDEX_ITEM_SELECT);
            CellRangeAddressList itemAddressList = new CellRangeAddressList(); itemAddressList.addCellRangeAddress(itemRange);
            XSSFDataValidation itemDataValidation = (XSSFDataValidation) dvHelper.createValidation(itemDvConstraint, itemAddressList);
            itemDataValidation.setShowErrorBox(true); itemDataValidation.setErrorStyle(DataValidation.ErrorStyle.STOP);
            itemDataValidation.createErrorBox("Invalid Item", "Please select a valid Spending Item from the dropdown list.");
            itemDataValidation.setEmptyCellAllowed(true); detailSheet.addValidationData(itemDataValidation);
            // Date picker
            XSSFDataValidationConstraint dateDvConstraint = (XSSFDataValidationConstraint) dvHelper.createDateConstraint(
                    DataValidationConstraint.OperatorType.BETWEEN, "DATE(1900,1,1)", "DATE(9999,12,31)", "yyyy-mm-dd");
            CellRangeAddress dateRange = new CellRangeAddress(firstDataRow, lastDataRow, DETAIL_COL_INDEX_TRANS_TIME, DETAIL_COL_INDEX_TRANS_TIME);
            CellRangeAddressList dateAddressList = new CellRangeAddressList(); dateAddressList.addCellRangeAddress(dateRange);
            XSSFDataValidation dateDataValidation = (XSSFDataValidation) dvHelper.createValidation(dateDvConstraint, dateAddressList);
            dateDataValidation.setShowErrorBox(true); dateDataValidation.setErrorStyle(DataValidation.ErrorStyle.STOP);
            dateDataValidation.createErrorBox("Invalid Date", "Please enter a valid date (e.g., YYYY-MM-DD) or select from the calendar.");
            dateDataValidation.setEmptyCellAllowed(true); detailSheet.addValidationData(dateDataValidation);

            // 8. Auto-size / Set Column Widths (Giữ nguyên)
            // ... (code set column widths) ...
            detailSheet.setColumnWidth(DETAIL_COL_INDEX_ITEM_SELECT, 40 * 256);
            detailSheet.setColumnWidth(DETAIL_COL_INDEX_AMOUNT, 20 * 256);
            detailSheet.setColumnWidth(DETAIL_COL_INDEX_TRANS_TIME, 20 * 256);
            detailSheet.setColumnWidth(DETAIL_COL_INDEX_DESCRIPTION, 45 * 256);
            detailSheet.setColumnWidth(DETAIL_COL_INDEX_PROOF_URL, 45 * 256);

            // 9. Protect Sheet (Quan trọng)
            detailSheet.protectSheet(""); // Mật khẩu trống


            // 10. Write to output stream (Giữ nguyên)
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());

        } catch (Exception e) {
            logger.error("Error generating Spending Detail template for Project ID {}: {}", projectId, e.getMessage(), e);
            throw new IOException("Failed to generate spending detail template: " + e.getMessage(), e);
        }
    }
    private Cell createCell(Row row, int columnIdx, Object value, CellStyle style) {
        Cell cell = row.createCell(columnIdx);
        if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else if (value instanceof Date) {
            cell.setCellValue((Date) value);
        } else if (value instanceof Calendar) {
            cell.setCellValue((Calendar) value);
        } else if (value instanceof RichTextString) {
            cell.setCellValue((RichTextString) value);
        } // Thêm các kiểu khác nếu cần
        if (style != null) {
            cell.setCellStyle(style);
        }
        return cell;
    }

    /**
     * Nhập (import) các Spending Details từ một MultipartFile Excel cho một project cụ thể.
     *
     * @param projectId UUID của project.
     * @param file      Đối tượng MultipartFile chứa file Excel (.xlsx) được upload.
     * @return Một List các đối tượng SpendingDetail đã được parse (chưa lưu vào DB).
     * @throws IOException              Nếu có lỗi khi đọc file.
     * @throws IllegalArgumentException Nếu file không hợp lệ (null, trống, sai định dạng),
     *                                  không tìm thấy project/plan/items, sheet bị thiếu/không hợp lệ,
     *                                  hoặc dữ liệu bắt buộc bị thiếu/không hợp lệ trong một dòng.
     */
    // @Transactional
    public List<SpendingDetail> importSpendingDetails(@NotNull UUID projectId, @NotNull MultipartFile file)
            throws IOException, IllegalArgumentException {

        // --- 1. Kiểm tra file đầu vào ---
        validateInputFile(file);

        // --- 2. Lấy Project và Spending Plan (Logic giống trước) ---
        Project project = projectRepository.findWithEssentialById(projectId);

        SpendingPlan activePlan = spendingPlanRepository.findByProjectId(projectId); // Hoặc logic tìm plan chính xác của bạn
        if (activePlan == null) {
            throw new IllegalArgumentException("Import failed: No active or relevant Spending Plan found for Project ID: " + projectId);
        }

        // --- 3. Lấy Spending Items và Tạo Map Lookup (Logic giống trước, bao gồm lọc) ---
        Map<String, SpendingItem> itemLookupMap = createItemLookupMap(activePlan.getId());

        List<SpendingDetail> importedDetails = new ArrayList<>();
        DataFormatter dataFormatter = new DataFormatter();

        // --- 4. Đọc và xử lý file Excel ---
        // Sử dụng try-with-resources để đảm bảo InputStream được đóng
        try (InputStream excelInputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(excelInputStream)) { // Mở workbook từ InputStream

            Sheet sheet = workbook.getSheet(DETAIL_SHEET_NAME);
            if (sheet == null) {
                throw new IllegalArgumentException("Import failed: Sheet '" + DETAIL_SHEET_NAME + "' not found in the workbook.");
            }

            int lastRowNum = sheet.getLastRowNum();
            logger.info("Processing file '{}', sheet '{}'. Header row: {}, First data row: {}, Last row index: {}",
                    file.getOriginalFilename(), DETAIL_SHEET_NAME, DETAIL_HEADER_ROW_INDEX, FIRST_DATA_ROW_INDEX, lastRowNum);

            // --- 5. Duyệt qua các dòng dữ liệu (Logic giống trước) ---
            for (int rowIndex = FIRST_DATA_ROW_INDEX; rowIndex <= lastRowNum; rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) {
                    continue; // Bỏ qua dòng trống
                }

                Cell itemCell = row.getCell(DETAIL_COL_INDEX_ITEM_SELECT, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                String itemName = getCellStringValue(itemCell, dataFormatter);

                if (itemName == null || itemName.trim().isEmpty()) {
                    logger.info("Stopping import at row {} due to empty Spending Item.", rowIndex + 1);
                    break; // Dừng nếu cột bắt buộc đầu tiên trống
                }

                try {
                    SpendingDetail detail = parseSpendingDetailRow(row, project, itemLookupMap, dataFormatter, rowIndex);
                    importedDetails.add(detail);
                    logger.debug("Successfully parsed row {}", rowIndex + 1);
                } catch (IllegalArgumentException | DateTimeParseException | ArithmeticException e) {
                    logger.error("Error processing row {}: {}", rowIndex + 1, e.getMessage());
                    // Ném lại lỗi để dừng toàn bộ quá trình import (hoặc thay đổi logic xử lý lỗi tại đây)
                    throw new IllegalArgumentException("Error processing row " + (rowIndex + 1) + ": " + e.getMessage(), e);
                }
            } // Kết thúc vòng lặp duyệt dòng

        } catch (IOException e) {
            logger.error("IOException during Excel processing for file '{}': {}", file.getOriginalFilename(), e.getMessage(), e);
            throw new IOException("Error reading Excel file '" + file.getOriginalFilename() + "': " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected error during Excel import for file '{}': {}", file.getOriginalFilename(), e.getMessage(), e);
            // Bắt các lỗi POI khác hoặc lỗi không mong muốn
            if (e instanceof IllegalArgumentException) { // Tránh gói lại IllegalArgumentException đã có thông tin dòng
                throw e;
            }
            throw new RuntimeException("Unexpected error during Excel import processing file '" + file.getOriginalFilename() + "': " + e.getMessage(), e);
        }

        logger.info("Successfully parsed {} spending details from file '{}' for project {}.",
                importedDetails.size(), file.getOriginalFilename(), projectId);
        return importedDetails;
    }

    /**
     * Kiểm tra tính hợp lệ của file upload.
     *
     * @param file MultipartFile cần kiểm tra.
     * @throws IllegalArgumentException Nếu file không hợp lệ.
     */
    private void validateInputFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Import failed: No file uploaded or file is empty.");
        }
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        if (!ALLOWED_EXTENSION.equalsIgnoreCase(extension)) {
            logger.error("Invalid file type uploaded: {}. Allowed type: {}", extension, ALLOWED_EXTENSION);
            throw new IllegalArgumentException("Import failed: Invalid file type. Only .xlsx files are allowed.");
        }
    }

    /**
     * Tạo Map để tra cứu SpendingItem theo tên (đã chuẩn hóa và lọc).
     * @param spendingPlanId UUID của SpendingPlan.
     * @return Map với key là tên item viết thường, value là đối tượng SpendingItem.
     * @throws IllegalArgumentException Nếu không tìm thấy item hợp lệ nào.
     */
    private Map<String, SpendingItem> createItemLookupMap(UUID spendingPlanId) {
        List<SpendingItem> spendingItems = spendingItemRepository.findBySpendingPlanId(spendingPlanId);
        if (spendingItems == null || spendingItems.isEmpty()) {
            logger.warn("No spending items found for plan {} during import.", spendingPlanId);
            throw new IllegalArgumentException("Import failed: No Spending Items found for the active Spending Plan (ID: " + spendingPlanId + ").");
        }

        final String phraseToExcludeLower = PHRASE_TO_EXCLUDE.toLowerCase();
        Map<String, SpendingItem> itemLookupMap = spendingItems.stream()
                .filter(item -> item.getItemName() != null &&
                        !item.getItemName().trim().toLowerCase().contains(phraseToExcludeLower))
                .collect(Collectors.toMap(
                        item -> item.getItemName().trim().toLowerCase(),
                        item -> item,
                        (existing, replacement) -> existing
                ));

        if (itemLookupMap.isEmpty()) {
            logger.warn("All spending items for plan {} were filtered out (contained '{}'). Cannot process import.", spendingPlanId, PHRASE_TO_EXCLUDE);
            throw new IllegalArgumentException("Import failed: No valid spending items (excluding '" + PHRASE_TO_EXCLUDE + "') available for lookup for Spending Plan (ID: " + spendingPlanId + ").");
        }
        return itemLookupMap;
    }

    /**
     * Parse dữ liệu từ một dòng trong Excel thành đối tượng SpendingDetail.
     *
     * @param row           Đối tượng Row cần parse.
     * @param project       Đối tượng Project liên quan.
     * @param itemLookupMap Map để tra cứu SpendingItem.
     * @param formatter     DataFormatter để đọc cell.
     * @param rowIndex      Chỉ số dòng (0-based) để báo lỗi.
     * @return Đối tượng SpendingDetail đã được parse.
     * @throws IllegalArgumentException Nếu dữ liệu bắt buộc thiếu hoặc không hợp lệ.
     * @throws DateTimeParseException   Nếu định dạng ngày giờ không hợp lệ.
     */
    private SpendingDetail parseSpendingDetailRow(Row row, Project project, Map<String, SpendingItem> itemLookupMap, DataFormatter formatter, int rowIndex)
            throws IllegalArgumentException, DateTimeParseException {

        SpendingDetail detail = new SpendingDetail();
        detail.setProject(project);

        // Item Name (đã kiểm tra không null/empty trước khi gọi hàm này)
        Cell itemCell = row.getCell(DETAIL_COL_INDEX_ITEM_SELECT); // Không cần check null vì đã check bên ngoài
        String itemName = getCellStringValue(itemCell, formatter);
        String cleanedItemName = itemName.trim().toLowerCase();
        SpendingItem mappedItem = itemLookupMap.get(cleanedItemName);
        if (mappedItem == null) {
            throw new IllegalArgumentException(String.format("Row %d: Spending Item '%s' not found or is excluded. Please select a valid item from the list.",
                    rowIndex + 1, itemName));
        }
        detail.setSpendingItem(mappedItem);

        // Amount
        Cell amountCell = row.getCell(DETAIL_COL_INDEX_AMOUNT, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        BigDecimal amount = getCellBigDecimalValue(amountCell, rowIndex);
        if (amount == null) {
            throw new IllegalArgumentException(String.format("Row %d: Amount is required.", rowIndex + 1));
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(String.format("Row %d: Amount cannot be negative.", rowIndex + 1));
        }
        detail.setAmount(amount);

        // Transaction Time
        Cell timeCell = row.getCell(DETAIL_COL_INDEX_TRANS_TIME, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        Instant transactionTime = getCellInstantValue(timeCell, formatter, rowIndex);
        detail.setTransactionTime(transactionTime);

        // Description
        Cell descCell = row.getCell(DETAIL_COL_INDEX_DESCRIPTION, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        detail.setDescription(getCellStringValue(descCell, formatter));

        // Proof URL
        Cell proofCell = row.getCell(DETAIL_COL_INDEX_PROOF_URL, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        detail.setProofImage(getCellStringValue(proofCell, formatter));

        return detail;
    }


    // --- Các hàm trợ giúp getCell... (giữ nguyên như trước) ---

    private String getCellStringValue(Cell cell, DataFormatter formatter) {
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            return null;
        }
        String value = formatter.formatCellValue(cell).trim();
        return value.isEmpty() ? null : value;
    }

    private BigDecimal getCellBigDecimalValue(Cell cell, int rowIndex) {
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            return null;
        }
        try {
            if (cell.getCellType() == CellType.NUMERIC) {
                if (DateUtil.isCellDateFormatted(cell)) {
                    throw new IllegalArgumentException(String.format("Row %d: Expected a numeric amount but found a date format in Amount column.", rowIndex + 1));
                }
                return BigDecimal.valueOf(cell.getNumericCellValue()).setScale(2, BigDecimal.ROUND_HALF_UP);
            } else if (cell.getCellType() == CellType.STRING) {
                String stringValue = cell.getStringCellValue().trim();
                if (stringValue.isEmpty()) return null;
                stringValue = stringValue.replace(".", ""); // Loại bỏ dấu chấm hàng nghìn
                stringValue = stringValue.replace(",", "."); // Thay dấu phẩy thập phân
                return new BigDecimal(stringValue).setScale(2, BigDecimal.ROUND_HALF_UP);
            } else if (cell.getCellType() == CellType.FORMULA) {
                CellValue cellValue = cell.getSheet().getWorkbook().getCreationHelper()
                        .createFormulaEvaluator().evaluate(cell);
                if (cellValue.getCellType() == CellType.NUMERIC) {
                    return BigDecimal.valueOf(cellValue.getNumberValue()).setScale(2, BigDecimal.ROUND_HALF_UP);
                } else {
                    throw new IllegalArgumentException(String.format("Row %d: Formula in Amount column did not evaluate to a number.", rowIndex + 1));
                }
            } else {
                throw new IllegalArgumentException(String.format("Row %d: Unsupported cell type '%s' for Amount column.", rowIndex + 1, cell.getCellType()));
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(String.format("Row %d: Invalid numeric format for Amount: '%s'.", rowIndex + 1, getCellStringValue(cell, new DataFormatter())), e);
        } catch (IllegalStateException e) {
            throw new IllegalArgumentException(String.format("Row %d: Cannot get numeric value from Amount cell: %s", rowIndex + 1, e.getMessage()), e);
        }
    }


    private Instant getCellInstantValue(Cell cell, DataFormatter formatter, int rowIndex) {
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            return null;
        }
        try {
            if (cell.getCellType() == CellType.NUMERIC) {
                if (DateUtil.isCellDateFormatted(cell)) {
                    Date javaUtilDate = cell.getDateCellValue();
                    return javaUtilDate != null ? javaUtilDate.toInstant() : null;
                } else {
                    throw new IllegalArgumentException(String.format("Row %d: Expected a date format but found a plain number in Transaction Time column.", rowIndex + 1));
                }
            } else if (cell.getCellType() == CellType.STRING) {
                String dateString = cell.getStringCellValue().trim();
                if (dateString.isEmpty()) {
                    return null;
                }
                LocalDate localDate = LocalDate.parse(dateString, DATE_TIME_FORMATTER);
                return localDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
            } else {
                throw new IllegalArgumentException(String.format("Row %d: Unsupported cell type '%s' for Transaction Time column.", rowIndex + 1, cell.getCellType()));
            }
        } catch (DateTimeParseException e) {
            throw new DateTimeParseException(
                    String.format("Row %d: Invalid date format for Transaction Time: '%s'. Expected format: %s",
                            rowIndex + 1, formatter.formatCellValue(cell), DATE_FORMAT_PATTERN),
                    e.getParsedString(), e.getErrorIndex(), e);
        } catch (IllegalStateException e) {
            throw new IllegalArgumentException(String.format("Row %d: Cannot get date value from Transaction Time cell: %s", rowIndex + 1, e.getMessage()), e);
        }
    }

}