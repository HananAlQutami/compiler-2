package SYMBOL_TABLE;

public class MissingTemplateDeclarationError extends SemanticError {
    private String componentName;
    private int lineNumber;

    public MissingTemplateDeclarationError(String componentName, int lineNumber) {
        super();
        this.componentName = componentName;
        this.lineNumber = lineNumber;
        this.message = "خطأ دلالي: المكون '" + componentName +
                "' يجب أن يحتوي على تعريف template (لم يتم العثور على template). (سطر: " + lineNumber + ")";

        Row row = new Row();
        row.setType("MissingTemplateDeclaration");
        row.setValue(componentName);
        this.symbolTable.getRows().add(row);
    }

    @Override
    public void print() {
        System.out.println(message);
        System.out.println("الجدول الرمزي الخاص بهذا الخطأ:");
        symbolTable.printyy();
    }

    public String getComponentName() {
        return componentName;
    }

    public int getLineNumber() {
        return lineNumber;
    }
}
