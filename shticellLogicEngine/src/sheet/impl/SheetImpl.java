package sheet.impl;

import exception.InvalidCoordinateException;
import exception.RangeExistsException;
import exception.RangeInUseException;
import impl.engine.CellDTO;
import sheet.api.Sheet;
import sheet.cell.api.Cell;
import sheet.cell.impl.CellImpl;
import sheet.command.filter.SheetFilter;
import sheet.coordinate.Coordinate;
import sheet.coordinate.CoordinateFactory;
import sheet.range.ranges.Ranges;
import sheet.command.sort.SheetSorter;
import sheet.structure.impl.SheetLayout;
import sheet.version.VersionManager;
import java.io.*;
import java.util.*;

public class SheetImpl implements Sheet, Serializable {

    private final String sheetName;
    private Map<Coordinate, Cell> activeCells;
    private final SheetLayout sheetLayout;
    private int versionNumber;
    private Cell currentCalculatingCell;
    private final VersionManager versionManager;
    private final Ranges ranges;
    private Map<String, Integer> rangeUsageMap;
    private Map<Coordinate, String> backgroundColorMap;
    private Map<Coordinate, String> textColorMap;

    public SheetImpl(String name, SheetLayout sheetLayout) {
        this.sheetLayout = sheetLayout;
        this.activeCells = new HashMap<>();
        this.versionNumber = 1;
        this.sheetName = name;
        this.versionManager = new VersionManager();
        this.ranges = new Ranges(sheetLayout);
        this.rangeUsageMap = new HashMap<>();
        this.backgroundColorMap = new HashMap<>();
        this.textColorMap = new HashMap<>();
    }

    public String getName() { return sheetName; }

    public int getSheetHeight() { return sheetLayout.getRows(); }

    public int getSheetWidth() { return sheetLayout.getColumns(); }

    public int getCellHeight() { return sheetLayout.getCellSize().getRowsHeightUnits(); }

    public int getCellWidth() { return sheetLayout.getCellSize().getColumnWidthUnits(); }

    public SheetLayout getSheetLayout() { return sheetLayout; }

    public void coordinateWithinBounds(Coordinate coordinate) { sheetLayout.coordinateWithinBounds(coordinate); }

    @Override
    public boolean rangeExists(String rangeName) {
        return ranges.rangeExists(rangeName);
    }

    @Override
    public Set<Coordinate> getCoordinatesInRange(String rangeName) {
        return ranges.getCoordinatesInRange(rangeName);
    }

    @Override
    public void registerRangeUsage(String rangeName) {
        rangeUsageMap.put(rangeName, rangeUsageMap.getOrDefault(rangeName, 0) + 1);
    }

    @Override
    public Set<Cell> getRangeCells(String rangeName) {

        Set<Coordinate> rangeCoordinates = getCoordinatesInRange(rangeName);

        Set<Cell> cellsInRange = new HashSet<>();
        for (Coordinate coordinate : rangeCoordinates) {
            Cell cell = getCell(coordinate);
            if (cell != null) {
                cellsInRange.add(cell);
            }
        }

        return cellsInRange;
    }

    @Override
    public void deleteRange(String rangeName) throws RangeInUseException {
        resetRangeUsageMap();
        if (rangeUsageMap.containsKey(rangeName)) {
            throw new RangeInUseException("Cannot delete range '" + rangeName + "' as it is still in use.");
        }

        ranges.deleteRange(rangeName);
    }

    private void resetRangeUsageMap() {
        rangeUsageMap.clear();

        for (Cell cell : activeCells.values()) {
            cell.calculateEffectiveValue();
        }
    }

    @Override
    public void addRange(String rangeName, String from, String to) throws InvalidCoordinateException, RangeExistsException {
        ranges.addRange(rangeName, from, to);
        recalculateAndGetChangedCells();
    }

    @Override
    public Set<String> getRanges(){
        return ranges.getRangesNames();
    }

    //sort

    @Override
    public List<String> getAvailableColumnsForSort(String from, String to) throws InvalidCoordinateException {
        return SheetSorter.getAvailableColumnsForSort(this, from, to);
    }

    @Override
    public Sheet getSortedSheet(String from, String to, List<String> selectedColumns) throws InvalidCoordinateException {
        return SheetSorter.getSortedSheet(this, from, to, selectedColumns);
    }

    // filter

    @Override
    public List<String> getAvailableColumnsForFilter(String from, String to) throws InvalidCoordinateException {
        return SheetFilter.getAvailableColumnsForFilter(this, from, to);
    }

    @Override
    public Set<String> getDistinctValuesForColumnInRange(String column, String from, String to) throws InvalidCoordinateException {
        return SheetFilter.getDistinctValuesForColumnInRange(this, column, from, to);
    }

    @Override
    public Sheet getFilteredSheet(Map<String, List<String>> valuesForFilter, String from, String to) throws InvalidCoordinateException {
        return SheetFilter.getFilteredSheet(this, valuesForFilter, from, to);
    }

    @Override
    public int getVersion() {
        return versionNumber;
    }

    public Cell getCurrentCalculatingCell() { return currentCalculatingCell; }

    public void setCurrentCalculatingCell(Cell currentCalculatingCell) { this.currentCalculatingCell = currentCalculatingCell; }

    public Map<Coordinate, Cell> getActiveCellMap() { return activeCells; }

    @Override
    public Cell getCell(Coordinate coordinate) { return activeCells.get(coordinate);}

    public VersionManager getVersionManager() { return versionManager; }

    public void increaseVersion() { versionNumber ++; }

    public void setCellFromFile(Coordinate coordinate, String value){
        sheetLayout.coordinateWithinBounds(coordinate);
        Cell newCell = new CellImpl(coordinate.getRow(), coordinate.getColumn(), value, getVersion() , this);
        activeCells.put(coordinate, newCell);
        newCell.calculateEffectiveValue();
        recalculateAndGetChangedCells();
    }


    @Override
    public void updateCellValueWithoutCalculation(int row, int column, String originalValue) {
        Coordinate coordinate = CoordinateFactory.createCoordinate(row, column);
        activeCells.remove(coordinate);
        Cell newCell = new CellImpl(row, column, originalValue, 1, this);
        activeCells.put(coordinate, newCell);
    }

    @Override
    public void updateCellBackground(Coordinate coordinate, String backgroundColor) {
        if(backgroundColor.equals("#FFFFFF")){
            backgroundColorMap.remove(coordinate);
        } else {
            backgroundColorMap.put(coordinate, backgroundColor);
        }
    }

    @Override
    public void updateCellTextColor(Coordinate coordinate, String textColor) {
        if(textColor.equals("#000000")){
            textColorMap.remove(coordinate);
        } else {
            textColorMap.put(coordinate, textColor);
        }
    }

    public void setBackgroundColorMap(Map<Coordinate, String> backgroundColorMap) {
        this.backgroundColorMap = backgroundColorMap;
    }

    public void setTextColorMap(Map<Coordinate, String> textColorMap) {
        this.textColorMap = textColorMap;
    }

    @Override
    public void updateCellValueInDynamicAnalysis(Coordinate coordinate, String newCellValue) {
        Cell cell = getCell(coordinate);

        if (cell != null) {
            cell.setCellOriginalValue(newCellValue);
            recalculateAndGetChangedCells();
        } else {
            throw new RuntimeException("The cell at the specified coordinate does not exist.");
        }

    }

    public Map<Coordinate, String> getBackgroundColorMap() {
        return backgroundColorMap;
    }

    public Map<Coordinate, String> getTextColorMap() {
        return textColorMap;
    }

    @Override
    public SheetImpl updateCellValueAndCalculate(int row, int column, String value, String currentUsername) {
        Coordinate coordinate = CoordinateFactory.createCoordinate(row, column);
        SheetImpl newSheetVersion = copySheet();
        int newVersionNumber = newSheetVersion.getVersion() + 1;

        newSheetVersion.updateOrCreateCell(coordinate, row, column, value, newVersionNumber, currentUsername);
        int numOfCellsThatHaveChanged = 1;

        newSheetVersion.handleEmptyCell(coordinate, value);

        try {
            List<Cell> cellsThatHaveChanged = newSheetVersion.recalculateAndGetChangedCells();
            newSheetVersion.increaseVersion();
            cellsThatHaveChanged.forEach(cell -> cell.updateVersion(newVersionNumber));
            numOfCellsThatHaveChanged += cellsThatHaveChanged.size();
            newSheetVersion.updateVersionManager(numOfCellsThatHaveChanged);

            return newSheetVersion;

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    private void handleEmptyCell(Coordinate coordinate, String value) {
        if(value.isEmpty()){
            removeCoordinate(coordinate);
        }
    }

    private void updateOrCreateCell(Coordinate coordinate, int row, int column, String value, int newVersionNumber, String currentUsername) {
        Cell cell = activeCells.get(coordinate);

        if (cell != null) {
            cell.removeDependencies();
            cell.setCellOriginalValue(value);
            cell.updateVersion(newVersionNumber);
            cell.calculateEffectiveValue();

        } else {
            cell = new CellImpl(row, column, value, newVersionNumber, this);
            activeCells.put(coordinate, cell);
            cell.calculateEffectiveValue();

        }

        cell.setLastUpdatedBy(currentUsername);
    }

    private void updateVersionManager(int cellsThatHaveChanged) {
        versionManager.setNumberOfChangedCellsInVersion(versionNumber, cellsThatHaveChanged);
        versionManager.setVersion(versionNumber,this);
    }

    private void removeCoordinate(Coordinate coordinate) {
        Cell cell = activeCells.get(coordinate);

        if(cell != null){
            cell.removeDependencies();
        }

        activeCells.remove(coordinate);
        backgroundColorMap.remove(coordinate);
        textColorMap.remove(coordinate);

    }

    private List<Cell> orderCellsForCalculation() {
        Map<Cell, List<Cell>> graph = new HashMap<>();
        Map<Cell, Integer> inDegree = new HashMap<>();

        for (Cell cell : activeCells.values()) {
            inDegree.put(cell, 0); // Initialize in-degrees to 0
        }

        for (Cell cell : activeCells.values()) {
            for (Cell dependent : cell.getInfluencingOn()) {
                graph.computeIfAbsent(cell, k -> new ArrayList<>()).add(dependent);
                inDegree.put(dependent, inDegree.get(dependent) + 1);
            }
        }

        Queue<Cell> queue = new LinkedList<>();
        for (Map.Entry<Cell, Integer> entry : inDegree.entrySet()) {
            if (entry.getValue() == 0) {
                queue.add(entry.getKey());
            }
        }

        List<Cell> sortedCells = new ArrayList<>();
        while (!queue.isEmpty()) {
            Cell cell = queue.poll();
            sortedCells.add(cell);

            if (graph.containsKey(cell)) {
                for (Cell influenced : graph.get(cell)) {
                    inDegree.put(influenced, inDegree.get(influenced) - 1);
                    if (inDegree.get(influenced) == 0) {
                        queue.add(influenced);
                    }
                }
            }
        }

        if (sortedCells.size() != activeCells.size()) {
            throw new RuntimeException("Circular dependency detected!");
        }

        return sortedCells;
    }


    public SheetImpl copySheet() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);
            oos.close();

            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
            return (SheetImpl) ois.readObject();
        } catch (Exception e) {
            throw new RuntimeException("Failed to copy sheet", e);
        }
    }

    private List<Cell> recalculateAndGetChangedCells(){
        List<Cell> cellsThatHaveChanged = new ArrayList<>();

        for (Cell cell : orderCellsForCalculation()) {
            setCurrentCalculatingCell(cell);

            if (cell.calculateEffectiveValue()) {
                cellsThatHaveChanged.add(cell);
            }
        }

        return cellsThatHaveChanged;
    }

    public CellDTO checkIfCellAllowsDynamicAnalysis(Coordinate selectedCoordinate) {
        Cell cell = activeCells.get(selectedCoordinate);
        if (cell != null) {
            if (isNumeric(cell.getOriginalValue())) {
                return CellDTO.createCellDTO(cell);
            } else {
                throw new RuntimeException("The selected cell must have an original value that is a numerical value.");
            }
        } else {
            throw new RuntimeException("The selected cell is empty.");
        }
    }

    private boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }

        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}


