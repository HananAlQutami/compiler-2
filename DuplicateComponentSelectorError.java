package SYMBOL_TABLE;

public class DuplicateComponentSelectorError extends SemanticError {
    private String selector;
    private int firstOccurrenceLine;
    private int duplicateLine;

    public DuplicateComponentSelectorError(String selector, int firstOccurrenceLine, int duplicateLine) {
        super();
        this.selector = selector;
        this.firstOccurrenceLine = firstOccurrenceLine;
        this.duplicateLine = duplicateLine;
        this.message = "خطأ دلالي: تم تعريف المكون بالـ selector '" + selector +
                "' مسبقاً في السطر " + firstOccurrenceLine +
                " والتعريف الحالي في السطر " + duplicateLine + ".";


        Row row = new Row();
        row.setType("DuplicateComponentSelector");
        row.setValue(selector);
        this.symbolTable.getRows().add(row);
    }

    @Override
    public void print() {
        System.out.println(message);
        System.out.println("الجدول الرمزي الخاص بهذا الخطأ:");
        symbolTable.printyy();
    }

    // يمكن إضافة getters إذا احتجت للوصول للمعلومات لاحقاً
    public String getSelector() {
        return selector;
    }

    public int getFirstOccurrenceLine() {
        return firstOccurrenceLine;
    }

    public int getDuplicateLine() {
        return duplicateLine;
    }
}
