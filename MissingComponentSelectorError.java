package SYMBOL_TABLE;

public class MissingComponentSelectorError extends SemanticError {
    private String componentName;
    private int lineNumber;

    public MissingComponentSelectorError(String componentName, int lineNumber) {
        super();
        this.componentName = componentName;
        this.lineNumber = lineNumber;

        if (componentName != null && !componentName.isEmpty()) {
            this.message = "الخطأ الدلالي: لم يتم تحديد selector للمكون '"
                    + componentName + "' في السطر " + lineNumber + ".";
        } else {
            this.message = "الخطأ الدلالي: لم يتم تحديد selector للمكون غير المعرّف في السطر "
                    + lineNumber + ".";
        }

        Row row = new Row();
        row.setType("MissingComponentSelector");
        row.setValue(componentName != null ? componentName : "UnknownComponent");
        this.symbolTable.getRows().add(row);
    }

    @Override
    public void print() {
        System.out.println(message);
        System.out.println(" the Symbol_table_TO_THIS_ERROR_IS");
        symbolTable.printyy();
    }

    public String getComponentName() {
        return componentName;
    }

    public int getLineNumber() {
        return lineNumber;
    }
}
