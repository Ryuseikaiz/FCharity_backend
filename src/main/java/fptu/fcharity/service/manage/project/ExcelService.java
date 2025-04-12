package fptu.fcharity.service.manage.project;

import fptu.fcharity.entity.*;

import fptu.fcharity.entity.SpendingItem; // Assuming entity location

import fptu.fcharity.repository.manage.project.ProjectRepository;
import fptu.fcharity.response.project.SpendingPlanReaderResponse;
import fptu.fcharity.utils.constants.request.RequestSupportType;
import lombok.NonNull;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
import java.util.*;

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
    private final ProjectRepository projectRepository; // Inject if using projectId

    public ExcelService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    /**
     * Generates an Excel template for creating a new Spending Plan and its items,
     * based on a given Project context. Instructions are at the top.
     * Multiple empty rows are provided for Spending Items.
     *
     * @param projectId The Project ID for context.
     * @return ByteArrayInputStream containing the Excel template data.
     * @throws IOException If an error occurs during workbook creation.
     * @throws IllegalArgumentException If the project is not found.
     */
    public ByteArrayInputStream generateNewSpendingPlanTemplate(@NonNull UUID projectId) throws IOException {

        Project project = projectRepository.findWithEssentialById(projectId);
        if (project == null) {
            throw new IllegalArgumentException("Project not found with ID: " + projectId);
        }

        // --- Calculate Max Extra Cost ---
        String maxExtraCostPercentage;
        String supportType = null;
        HelpRequest request = project.getRequest();
        if (request != null) {
            supportType = request.getSupportType(); // *** REPLACE WITH ACTUAL LOGIC TO GET SUPPORT TYPE ***
            if (supportType == null || supportType.trim().isEmpty()) {
                // Use English in log message
                logger.warn("Project '{}' has HelpRequest but supportType is missing/empty. Defaulting Max Extra Cost to 10%.", project.getProjectName());
                supportType = null;
            }
        } else {
            // Use English in log message
            logger.warn("Project '{}' has no associated HelpRequest. Defaulting Max Extra Cost to 10%.", project.getProjectName());
        }
        // Assume RequestSupportType.MONEY is a String or Enum.name()
        boolean isMoneyRequest = RequestSupportType.MONEY.equalsIgnoreCase(supportType);
        maxExtraCostPercentage = isMoneyRequest ? "0%" : "10%";
        // -------------------------------------------------------------------

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet(SHEET_NAME);

            // --- Create Styles (Styles remain the same) ---
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
            CellStyle instructionStyle = createCellStyle(workbook, true, instructionFont, BorderStyle.NONE, VerticalAlignment.TOP, true, null, null); // Wrap text, no border

            int currentRowIndex = 0;
            int numInitialItemRows = 50; // Number of initial sample item rows

            // --- Section 0: Instructions (At the top) ---
            int instructionStartRow = currentRowIndex;
            Row instructionRow = sheet.createRow(instructionStartRow);
            instructionRow.setHeightInPoints(4 * sheet.getDefaultRowHeightInPoints()); // Increase height for instructions

            // Pre-calculate the estimated starting row index for the items table for the instructions
            // Instruction (3 rows) + Separator (1 row) + Project Info (2 rows) + Separator (1 row) + Plan Input (2 rows) + Separator (1 row) + Item Header (1 row) = 11 rows before first item data
            int estimatedFirstItemDataRowIndex = instructionStartRow + 3 + 1 + 2 + 1 + 2 + 1 + 1; // Index starts from 0

            // Use English for instruction text
            Cell instructionCell = createCell(instructionRow, 0, // Start from column 0
                    "Instructions:\n"
                            + "1. DO NOT MODIFY cells with gray backgrounds or labels (Project Name, Max Extra Cost, Plan Name*, Description, column headers). Only enter data in the empty cells to the right of Plan Name*/Description and in the empty rows in the Spending Items table below.\n"
                            + "2. Enter the Plan Name (required) and Description.\n"
                            + "3. Enter the details for spending items in the empty rows starting from row " + (estimatedFirstItemDataRowIndex + 1) + ".\n" // +1 for user-friendly row number
                            + "4. To add more spending items than the initial " + numInitialItemRows + " rows provided, select the last row you entered and insert a new row (e.g., right-click > Insert > Sheet Rows).",
                    instructionStyle);
            // Merge the instruction cell across the necessary width (e.g., width of the items table)
            sheet.addMergedRegion(new CellRangeAddress(instructionStartRow, instructionStartRow + 2, // Merge 3 rows
                    0, NUM_ITEM_DATA_COLUMNS - 1)); // Merge across the number of columns of the items table
            currentRowIndex = instructionStartRow + 3; // Increment row index after merging
            currentRowIndex++; // Add 1 empty row after instructions


            // --- Section 1: Project Info (Read-Only) ---
            createLabelValueRow(sheet, currentRowIndex++, "Project Name:", project.getProjectName(), lockedLabelStyle, lockedValueStyle);
            createLabelValueRow(sheet, currentRowIndex++, "Max Extra Cost:", maxExtraCostPercentage, lockedLabelStyle, lockedValueStyle);
            currentRowIndex++; // Empty separator row


            // --- Section 2: Spending Plan Input (Editable) ---
            Row planNameInputRow = sheet.createRow(currentRowIndex++);
            createCell(planNameInputRow, INPUT_COL_LABEL, "Plan Name *:", lockedLabelStyle);
            createCell(planNameInputRow, INPUT_COL_VALUE, "", unlockedInputStyle); // Leave blank, no placeholder needed

            Row planDescInputRow = sheet.createRow(currentRowIndex++);
            planDescInputRow.setHeightInPoints(3 * sheet.getDefaultRowHeightInPoints()); // Moderate height
            createCell(planDescInputRow, INPUT_COL_LABEL, "Description:", lockedLabelStyle);
            createCell(planDescInputRow, INPUT_COL_VALUE, "", unlockedInputStyle); // Leave blank
            currentRowIndex++; // Empty separator row


            // --- Section 3: Spending Items Table ---
            int itemTableHeaderRowIndex = currentRowIndex++; // Header for the items table
            int firstItemDataRowIndex = currentRowIndex;    // First item data row index (should match calculation)

            // Row X: Item Table Header (Locked)
            Row itemHeaderRow = sheet.createRow(itemTableHeaderRowIndex);
            // Headers are already in English
            String[] itemHeaders = {ITEM_HEADER_ITEM_NAME, ITEM_HEADER_ESTIMATED_COST, ITEM_HEADER_NOTE};
            for (int i = 0; i < itemHeaders.length; i++) {
                Cell cell = itemHeaderRow.createCell(i);
                cell.setCellValue(itemHeaders[i]);
                cell.setCellStyle(itemHeaderStyle);
            }

            // Rows X+1 to X+numInitialItemRows: Item Data Rows (Editable, Blank)
            for (int i = 0; i < numInitialItemRows; i++) {
                Row dataRow = sheet.createRow(currentRowIndex++);
                // Create blank cells but apply unlocked style
                createCell(dataRow, ITEM_COL_INDEX_ITEM_NAME, "", unlockedItemStyle);
                createCell(dataRow, ITEM_COL_INDEX_ESTIMATED_COST, 0.0, costItemStyle); // Can be 0 or blank
                createCell(dataRow, ITEM_COL_INDEX_NOTE, "", unlockedItemStyle);
            }

            // --- Auto-size / Set Column Widths ---
            sheet.autoSizeColumn(INPUT_COL_LABEL); // Auto-size label column
            sheet.setColumnWidth(INPUT_COL_VALUE, 60 * 256); // Plan value/description column
            sheet.setColumnWidth(ITEM_COL_INDEX_ITEM_NAME, 40 * 256); // Item Name column
            sheet.setColumnWidth(ITEM_COL_INDEX_ESTIMATED_COST, 20 * 256); // Cost column
            sheet.setColumnWidth(ITEM_COL_INDEX_NOTE, 45 * 256); // Note column


            // --- Protect Sheet ---
            sheet.protectSheet(""); // Use null for no password


            // --- Write to output stream ---
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    // --- Helper methods (Keep as they are, logic is language-independent) ---
    private static Cell createCell(Row row, int column, String value, CellStyle style) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value);
        if (style != null) {
            cell.setCellStyle(style);
        }
        return cell;
    }
    private static Cell createCell(Row row, int column, double value, CellStyle style) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value);
        if (style != null) {
            cell.setCellStyle(style);
        }
        return cell;
    }
    private static void createLabelValueRow(Sheet sheet, int rowIndex, String label, String value, CellStyle labelStyle, CellStyle valueStyle) {
        Row row = sheet.createRow(rowIndex);
        createCell(row, INPUT_COL_LABEL, label, labelStyle);
        createCell(row, INPUT_COL_VALUE, value, valueStyle);
    }
    private static CellStyle createCellStyle(Workbook workbook, boolean locked, Font font, BorderStyle border,
                                             VerticalAlignment valign, boolean wrapText, Short fgColor, FillPatternType fpType) {
        CellStyle style = workbook.createCellStyle();
        style.setLocked(locked);
        if (font != null) style.setFont(font);
        if (border != null) {
            style.setBorderBottom(border);
            style.setBorderTop(border);
            style.setBorderLeft(border);
            style.setBorderRight(border);
        }
        if (valign != null) style.setVerticalAlignment(valign);
        style.setWrapText(wrapText);
        if (fgColor != null) style.setFillForegroundColor(fgColor);
        if (fpType != null) style.setFillPattern(fpType);
        return style;
    }
    private static CellStyle createCellStyle(Workbook workbook, boolean locked, Font font, BorderStyle border,
                                             VerticalAlignment valign, Short fgColor, FillPatternType fpType) {
        return createCellStyle(workbook, locked, font, border, valign, false, fgColor, fpType); // Default no wrap
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
}