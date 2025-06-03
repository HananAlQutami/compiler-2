package SYMBOL_TABLE;

public class UndefinedVariableError extends SemanticError {
    private String variableName;
    private int lineNumber;

    public UndefinedVariableError(String variableName, int lineNumber) {
        super();  // يقوم المُنشئ الأساسي بتهيئة الـ symbolTable الخاص بهذا الخطأ
        this.variableName = variableName;
        this.lineNumber = lineNumber;
        this.message = "خطأ دلالي: المتغير '" + variableName
                + "' المستخدم في تعبير Mustache غير معرف. (سطر: " + lineNumber + ")";

        // تسجيل المعلومات في جدول الرموز الخاص بالخطأ
        Row row = new Row();
        row.setType("UndefinedVariable");
        row.setValue(variableName);
        this.symbolTable.getRows().add(row);
    }

    @Override
    public void print() {
        System.out.println(message);
        System.out.println("الجدول الرمزي الخاص بهذا الخطأ:");
        symbolTable.printyy();
    }

    public String getVariableName() {
        return variableName;
    }

    public int getLineNumber() {
        return lineNumber;
    }
}
