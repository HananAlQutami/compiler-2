package SYMBOL_TABLE;

public class DuplicateImportPathError extends SemanticError {

    public DuplicateImportPathError(String path, int firstLine, int duplicateLine) {
        super();


        Row row1 = new Row();
        row1.setType("Import Path");
        row1.setValue(path);

        Row row2 = new Row();
        row2.setType("First Occurrence Line");
        row2.setValue(String.valueOf(firstLine));

        Row row3 = new Row();
        row3.setType("Duplicate Occurrence Line");
        row3.setValue(String.valueOf(duplicateLine));

        symbolTable.getRows().add(row1);
        symbolTable.getRows().add(row2);
        symbolTable.getRows().add(row3);


        this.message = "Duplicate import from the same path: '" + path +
                "' found at lines " + firstLine + " and " + duplicateLine;
    }

    @Override
    public void print() {
        System.out.println("Semantic Error: " + message);
        symbolTable.printyy();
    }
}