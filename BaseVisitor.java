package VISITOR;
import AST.*;
import SYMBOL_TABLE.*;
import ANTLR.AngularParser;
import ANTLR.AngularParserBaseVisitor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class BaseVisitor  extends AngularParserBaseVisitor {
    SymbolTable globalsymbolTable=new SymbolTable();

    //=====================
    // نفترض أن هذه الحقول موجودة في الكلاس (مثلاً في الـVisitor نفسه)
    private Map<String, Integer> importPaths = new HashMap<>(); // مسار الاستيراد -> رقم السطر الأول
    private List<SemanticError> semanticErrors = new ArrayList<>();
    //==========
    @Override

    public Program visitProgram(AngularParser.ProgramContext ctx){

        Program program = new Program();

        if(ctx.importStatement()!=null)
        {

            program.setImportStatement(visitImportStatement(ctx.importStatement()));

        }
        for (int i = 0; i < ctx.statment().size(); i++) {
            if (ctx.statment(i)!=null)
            {

                program.getSourceElement().add(visitStatment(ctx.statment(i)));
            }
        }
        this.globalsymbolTable.printyy();


        System.out.println("\n\n");
        printSemanticErrors();

/*
        SemanticError semanticerror =new SemanticError();
        semanticerror.setSymboltable(this.globalsymbolTable);
        semanticerror.check();
        semanticerror.print();
*/

        return program;

    }

    @Override
    public ImportStatmente visitImportStatement(AngularParser.ImportStatementContext ctx)
    {
        ImportStatmente importStatement = new ImportStatmente ();
        for (int i = 0; i < ctx.importFromBlock().size(); i++) {
            if (ctx.importFromBlock(i)!=null)
            {
                importStatement.getImportFromBlocks().add(visitImportFromBlock(ctx.importFromBlock(i)));
            }
        }
        return importStatement;

    }

    @Override
    public ImportFromBlock visitImportFromBlock(AngularParser.ImportFromBlockContext ctx) {
        ImportFromBlock importFromBlock = new ImportFromBlock();

        if (ctx.IDENTIFIER() != null) {
            importFromBlock.setIdentifier(ctx.IDENTIFIER().getText());
        }

        if (ctx.moduleItems() != null) {
            importFromBlock.setModuleItems(visitModuleItems(ctx.moduleItems()));
        }

        if (ctx.STRINGLITERAL() != null) {
            String importPath = ctx.STRINGLITERAL().getText();
            importFromBlock.setImportFrom(importPath);

            // نضيف مسار الاستيراد إلى جدول الرموز العام
            Row r8 = new Row();
            r8.setType("ImportPath");
            r8.setValue(importPath);
            globalsymbolTable.getRows().add(r8);

            // نأخذ رقم السطر (مثلاً رقم السطر من ctx)
            int line = ctx.getStart().getLine();

            // نتحقق هل هذا المسار تم استيراده سابقاً؟
            if (importPaths.containsKey(importPath)) {
                int firstLine = importPaths.get(importPath);
                // إذا نعم، نولد خطأ دلالي
                DuplicateImportPathError error = new DuplicateImportPathError(importPath, firstLine, line);
                semanticErrors.add(error);
            } else {
                // إذا لا، نضيفه إلى الخريطة مع رقم السطر الحالي
                importPaths.put(importPath, line);
            }
        }

        return importFromBlock;
    }

    @Override
    public ModuleItems visitModuleItems(AngularParser.ModuleItemsContext ctx) {
        ModuleItems moduleItems = new ModuleItems () ;

        if (ctx.COMPONENT()!= null)
            moduleItems.setComponent(ctx.COMPONENT().getText());

        if (ctx.IDENTIFIER(0)!= null)
            moduleItems.setIdentifier1(ctx.IDENTIFIER(0).getText());
        if (ctx.IDENTIFIER(1)!= null)
            moduleItems.setIdentifier2(ctx.IDENTIFIER(1).getText());
        return moduleItems ;
    }


    @Override
    public Statment visitStatment(AngularParser.StatmentContext ctx) {

        Statment statement= new Statment();
        if(ctx.functionDeclaration()!=null)
        {
            statement.setFunctionDeclaration(visitFunctionDeclaration(ctx.functionDeclaration()));
        }
        if(ctx.variableStatement()!=null)
        {
            statement.setVariableStatement(visitVariableStatement(ctx.variableStatement()));
        }
        if(ctx.expressionStatement()!=null)
        {
            statement.setExpressionStatement(visitExpressionStatement(ctx.expressionStatement()));
        }
        if(ctx.componentDeclaration()!=null)
        {
            statement.setComponentDeclaration(visitComponentDeclaration(ctx.componentDeclaration()));
        }
        if(ctx.classDeclaration()!=null)
        {
            statement.setClassDeclaration(visitClassDeclaration(ctx.classDeclaration()));
        }
        return statement;
    }










    @Override
    public ComponentDeclaration visitComponentDeclaration(AngularParser.ComponentDeclarationContext ctx) {
        ComponentDeclaration componentDeclaration = new ComponentDeclaration();

        if (ctx.componentAttributes() != null) {
            ComponentAttributes attributes = visitComponentAttributes(ctx.componentAttributes());
            componentDeclaration.setComponentAttributes(attributes);

            String selectorValue = null;
            String compName = null;

            for (ComponentAttribute compAttr : attributes.getComponentAttribute()) {
                if (compAttr != null && compAttr.getSelector() != null) {
                    selectorValue = compAttr.getSelector().toString();
                    compName = selectorValue;
                    break;
                }

            }
            if (compName == null || compName.trim().isEmpty()) {
                compName = "UnknownComponent";
            }

            // فحص وجود تعريف مسبق لنفس الـ selector في الجدول الرمزي العام

            boolean duplicateFound = false;
            int firstOccurrenceLine = -1;

            for (Row r : globalsymbolTable.getRows()) {
                if ("ComponentSelector".equals(r.getType()) && r.getValue().equals(selectorValue)) {
                    duplicateFound = true;


                    firstOccurrenceLine = 1;
                    break;
                }
            }

            // معالجة خطأ تكرار الـ selector


            if (duplicateFound) {
                DuplicateComponentSelectorError dupError = new DuplicateComponentSelectorError(selectorValue, firstOccurrenceLine, ctx.getStart().getLine());
                semanticErrors.add(dupError);
            } else {
                if (selectorValue == null || selectorValue.trim().isEmpty()) {
                    MissingComponentSelectorError selectorError = new MissingComponentSelectorError(compName, ctx.getStart().getLine());
                    semanticErrors.add(selectorError);
                } else {
                    Row row = new Row();
                    row.setType("ComponentSelector");
                    row.setValue(selectorValue);
                    globalsymbolTable.getRows().add(row);
                }
            }



            boolean templateFound = false;
            for (ComponentAttribute compAttr : attributes.getComponentAttribute()) {
                if (compAttr != null && compAttr.getTemplate() != null) {
                    templateFound = true;
                    break;
                }
            }
            if (!templateFound) {
                MissingTemplateDeclarationError templateError = new MissingTemplateDeclarationError(compName, ctx.getStart().getLine());
                semanticErrors.add(templateError);
            } else {
                Row row = new Row();
                row.setType("ComponentTemplate");
                row.setValue("Defined");
                globalsymbolTable.getRows().add(row);
            }


        }
        return componentDeclaration;
    }




    /*@Override
    public ComponentDeclaration visitComponentDeclaration(AngularParser.ComponentDeclarationContext ctx) {


        ComponentDeclaration componentDeclaration=new ComponentDeclaration();



        if(ctx.componentAttributes()!=null){
            componentDeclaration.setComponentAttributes(visitComponentAttributes(ctx.componentAttributes()));
        }
        /// /////////////


        return componentDeclaration;
    }*/

    @Override
    public Template visitTemplateDeclaration(AngularParser.TemplateDeclarationContext ctx) {
        Template template=new Template();
        if(ctx.TEMPLATE()!=null)
        {
            template.setTemplate(ctx.TEMPLATE().getText());
        }
        if(ctx.COLON()!=null){
            template.setColon(ctx.COLON().getText());
        }
        if (ctx.htmlElements() != null) {
            template.setHtmlElementsNode(visitHtmlElements(ctx.htmlElements()));
        }
        return template;
    }

    @Override
    public ComponentAttributes visitComponentAttributes(AngularParser.ComponentAttributesContext ctx) {
        ComponentAttributes componentAttributes=new ComponentAttributes();
        for (int i = 0; i < ctx.componentAttribute().size(); i++) {
            if (ctx.componentAttribute(i) != null) {
                componentAttributes.getComponentAttribute().add(visitComponentAttribute(ctx.componentAttribute(i)));
            }
        }
        return componentAttributes;
    }

    @Override
    public ComponentAttribute visitComponentAttribute(AngularParser.ComponentAttributeContext ctx) {
        ComponentAttribute componentAttribute=new ComponentAttribute();
        if(ctx.templateDeclaration()!=null){
            componentAttribute.setTemplate(visitTemplateDeclaration(ctx.templateDeclaration()));
        }
        if(ctx.selectorDeclaration()!=null){
            componentAttribute.setSelector(visitSelectorDeclaration(ctx.selectorDeclaration()));
        }
        if(ctx.standaloneDeclaration()!=null){
            componentAttribute.setStandalone(visitStandaloneDeclaration(ctx.standaloneDeclaration()));
        }
        if(ctx.importsDeclaration()!=null){
            componentAttribute.setImports(visitImportsDeclaration(ctx.importsDeclaration()));
        }
        if(ctx.stylesDeclaration()!=null){
            componentAttribute.setStyles(visitStylesDeclaration(ctx.stylesDeclaration()));
        }
        return componentAttribute;
    }

    @Override
    public HtmlElementsNode visitHtmlElements(AngularParser.HtmlElementsContext ctx) {
        HtmlElementsNode   htmlElementsNode= new HtmlElementsNode();
        for (int i = 0; i < ctx.htmlElement().size(); i++) {
            if (ctx.htmlElement(i) != null) {
                htmlElementsNode.getHtmlElements().add(visitHtmlElement(ctx.htmlElement(i)));
            }
        }
        return htmlElementsNode;
    }

    @Override
    public HtmlElementNode visitHtmlElement(AngularParser.HtmlElementContext ctx)
    {
        HtmlElementNode htmlElementNode= new HtmlElementNode();
        if(ctx.IDENTIFIER(0)!= null)
        {
            htmlElementNode.setTagName(ctx.IDENTIFIER(0).getText());
        }


        for (int i = 0; i < ctx.htmlAttribute().size(); i++) {
            if (ctx.htmlAttribute(i) != null) {
                htmlElementNode.getAttributes().add(visitHtmlAttribute(ctx.htmlAttribute(i)));
            }

        }
        if (ctx.htmlContent()!=null)
        {
            htmlElementNode.setContent(visitHtmlContent(ctx.htmlContent()));
        }
        if(ctx.IDENTIFIER(1)!= null)
        {
            htmlElementNode.setTagNameClose(ctx.IDENTIFIER(1).getText());
        }

        return htmlElementNode;

    }
    @Override
    public HtmlAttributeNode visitHtmlAttribute(AngularParser.HtmlAttributeContext ctx) {

        HtmlAttributeNode htmlAttributeNode = new HtmlAttributeNode();

        if (ctx.IDENTIFIER() != null) {
            String attributeName = ctx.IDENTIFIER().getText();
            htmlAttributeNode.setAttributeName(attributeName);
            boolean attributeNameExists = false;
            for (Row row : this.globalsymbolTable.getRows()) {
                if (row.getType().equals("htmlAttributeName") && row.getValue().equals(attributeName)) {
                    attributeNameExists = true;
                    break;
                }
            }
            if (!attributeNameExists) {
                Row row4 = new Row();
                row4.setType("htmlAttributeName");
                row4.setValue(attributeName);
                this.globalsymbolTable.getRows().add(row4);
            }
        }

        if (ctx.htmlAttributeValue() != null) {
            htmlAttributeNode.setAttributeValue(visitHtmlAttributeValue(ctx.htmlAttributeValue()));
        }
        if (ctx.CLASS() != null) {
            String attributeName = ctx.CLASS().getText();
            htmlAttributeNode.setAttributeName(attributeName);
        }

/// ////////////////////////////////////

        if (ctx.directive() != null) {
            String attributeName = ctx.directive().getText();
            htmlAttributeNode.setAttributeName(attributeName);


            HtmlAttributeValueNode valueNode = visitHtmlAttributeValue(ctx.htmlAttributeValue());
            String attrValue = valueNode.getValue();

            if ("*ngFor".equals(attributeName)) {

                if (attrValue == null || !attrValue.contains("let ")) {
                    MissingNgForVariableError error = new MissingNgForVariableError(attributeName, ctx.getStart().getLine());
                    semanticErrors.add(error);
                }
            }


            Row r9 = new Row();
            r9.setValue(attrValue);
            r9.setType(attributeName);
            globalsymbolTable.getRows().add(r9);

        } else if (ctx.IDENTIFIER() != null) {

            String attributeName = ctx.IDENTIFIER().getText();
            htmlAttributeNode.setAttributeName(attributeName);
            boolean attributeNameExists = false;
            for (Row row : this.globalsymbolTable.getRows()) {
                if ("htmlAttributeName".equals(row.getType()) && attributeName.equals(row.getValue())) {
                    attributeNameExists = true;
                    break;
                }
            }
            if (!attributeNameExists) {
                Row row4 = new Row();
                row4.setType("htmlAttributeName");
                row4.setValue(attributeName);
                this.globalsymbolTable.getRows().add(row4);
            }

            if (ctx.htmlAttributeValue() != null) {
                htmlAttributeNode.setAttributeValue(visitHtmlAttributeValue(ctx.htmlAttributeValue()));
            }


        }



        return htmlAttributeNode;
    }

    @Override
    public HtmlContentNode visitHtmlContent(AngularParser.HtmlContentContext ctx) {
        HtmlContentNode htmlContentNode= new HtmlContentNode();
        for (int i = 0; i < ctx.htmlElement().size(); i++) {
            if (ctx.htmlElement(i) != null) {
                htmlContentNode.getHtmlContent().add(visitHtmlElement(ctx.htmlElement(i)));
            }
        }
        for (int i = 0; i < ctx.singleExpression().size(); i++) {
            if (ctx.singleExpression(i) != null) {
                htmlContentNode.getExpContent().add(visitSingleExpression(ctx.singleExpression(i)));
            }
        }
        return  htmlContentNode;
    }

    @Override
    public HtmlAttributeValueNode visitHtmlAttributeValue(AngularParser.HtmlAttributeValueContext ctx) {
        HtmlAttributeValueNode htmlAttributeValueNode = new HtmlAttributeValueNode();

        if (ctx.STRINGLITERAL() != null) {

            htmlAttributeValueNode.setValue(ctx.STRINGLITERAL().getText());

        }

        for (int i = 0; i < ctx.singleExpression().size(); i++) {
            if (ctx.singleExpression(i) != null) {
                htmlAttributeValueNode.getExpressions().add(visitSingleExpression(ctx.singleExpression(i)));
            }
        }

        return htmlAttributeValueNode;
    }


    @Override
    public Expression visitSingleExpression(AngularParser.SingleExpressionContext ctx) {
        Expression expression =new Expression();
        if (ctx.literal() != null) {
            expression.setLiteralExpression(visitLiteral(ctx.literal()));
            return expression;
        }
        else if (ctx.indexarray() != null) {
            expression.setIndexArray( visitIndexarray(ctx.indexarray()));
            return expression;
        }
        else if (ctx.arrayLiteral() != null) {
            expression.setArrayLiteral( visitArrayLiteral(ctx.arrayLiteral()));
            return expression;
        }
        else if (ctx.objectLiteral() != null) {
            expression .setObjectLiteral(visitObjectLiteral(ctx.objectLiteral()));
            return expression ;
        }
        else if (ctx.htmlElements() != null) {
            expression.setHtmlElementsNode(visitHtmlElements(ctx.htmlElements()));
            return expression;
        }
        else if (ctx.IDENTIFIER() != null) {
            expression.setIdentifier(ctx.IDENTIFIER().getText());

            return expression;
        }
        else if (ctx.mustacheExpression() != null) {
            expression.setMustache(visitMustacheExpression(ctx.mustacheExpression()));

            return expression;
        }
        else if (ctx.singleExpressionCss() != null) {
            expression.setStyleContent(visitSingleExpressionCss(ctx.singleExpressionCss()));

            return expression;
        }
        else if (ctx.singleExpression().size() == 2 && ctx.DOT() != null) {
            Expression left = visitSingleExpression(ctx.singleExpression(0));
            expression.setLeft(left);
            Expression right = visitSingleExpression(ctx.singleExpression(1));
            expression.setRight(right);
            return  expression ;
        }

        else if (ctx.singleExpression().size() == 2 && ctx.ASSIGN() != null) {
            Expression left = visitSingleExpression(ctx.singleExpression(0));
            expression.setLeft(left);
            Expression right = visitSingleExpression(ctx.singleExpression(1));
            expression.setRight(right);
            return  expression ;
        }
        else if (ctx.singleExpression().size() == 2 && ctx.COLON() != null) {
            Expression left = visitSingleExpression(ctx.singleExpression(0));
            expression.setLeft(left);
            Expression right = visitSingleExpression(ctx.singleExpression(1));
            expression.setRight(right);
            return  expression ;
        }

        return expression;
    }

    @Override

    public LiteralExpression visitLiteral(AngularParser.LiteralContext ctx) {
        LiteralExpression literalExpression = new LiteralExpression();
        if (ctx.BOOLEANLITERAL() != null) {
            literalExpression.setBooleanLiteral(Boolean.parseBoolean(ctx.BOOLEANLITERAL().getText()));}
        else if (ctx.DECIMALLITERAL() != null) {
            literalExpression.setDecimalLiteral(Double.parseDouble(ctx.DECIMALLITERAL().getText()));}
        else if (ctx.STRINGLITERAL() != null) {
            literalExpression.setStringLiteral(ctx.STRINGLITERAL().getText());}

        else {
            literalExpression.setNullLiteral(null);}

        return literalExpression;
    }
    @Override
    public ObjectLiteral visitObjectLiteral(AngularParser.ObjectLiteralContext ctx) {
        ObjectLiteral objectLiteral = new ObjectLiteral();
        for (int i = 0; i < ctx.propertyAssignment().size(); i++) {
            if (ctx.propertyAssignment(i) != null) {
                objectLiteral.getProperties().add(visitPropertyAssignment(ctx.propertyAssignment(i)));
            }
        }
        return objectLiteral;

    }
    @Override
    public PropertyAssignment visitPropertyAssignment(AngularParser.PropertyAssignmentContext ctx) {
        PropertyAssignment propertyAssignment = new PropertyAssignment();
        if (ctx.singleExpression(0)!= null) {
            Expression key = visitSingleExpression(ctx.singleExpression(0));
            propertyAssignment.setKey(key);
        }
        if (ctx.singleExpression(1)!= null) {
            Expression value = visitSingleExpression(ctx.singleExpression(1));
            propertyAssignment.setValue(value);
        }
        return propertyAssignment;
    }

    @Override
    public ArrayLiteral visitArrayLiteral(AngularParser.ArrayLiteralContext ctx) {
        ArrayLiteral arrayLiteral = new ArrayLiteral();
        for (int i = 0; i < ctx.singleExpression().size(); i++) {
            if (ctx.singleExpression(i) != null) {
                arrayLiteral.getElements().add(visitSingleExpression(ctx.singleExpression(i)));

            }
        }
        return arrayLiteral;
    }
    @Override
    public FunctionDeclaration visitFunctionDeclaration(AngularParser.FunctionDeclarationContext ctx) {
        FunctionDeclaration functionDeclaration = new FunctionDeclaration();
        if(ctx.EXPORT()!=null)
        {
            functionDeclaration.setFunctionExport(ctx.EXPORT().getText());
        }
        if(ctx.IDENTIFIER()!=null)
        {
            functionDeclaration.setFunctionName(ctx.IDENTIFIER().getText());
            Row row=new Row();
            row.setType("FunctionName");
            row.setValue(functionDeclaration.getFunctionName());
            this.globalsymbolTable.getRows().add(row);

        }
        if(ctx.IDENTIFIER()!=null)
        {
            functionDeclaration.setFunctionName(ctx.IDENTIFIER().getText());

        }
        for (int i = 0; i < ctx.singleExpression().size(); i++) {
            if (ctx.singleExpression(i) != null) {
                functionDeclaration.getParameters().add(visitSingleExpression(ctx.singleExpression(i)));
            }
        }
        if (!ctx.singleExpression().isEmpty())
        {
            Row row = new Row();
            row.setType("functionParameters");
            row.setValue(functionDeclaration.getParameters().toString());
            this.globalsymbolTable.getRows().add(row);
        }
        for (int i = 0; i < ctx.statment().size(); i++) {
            if (ctx.statment(i) != null) {
                functionDeclaration.getBody().add(visitStatment(ctx.statment(i)));
            }
        }
        if(ctx.exportStatement()!=null)
        {
            functionDeclaration.setEx(visitExportStatement(ctx.exportStatement()));
        }
        return functionDeclaration;

    }

    @Override
    public VariableStatement visitVariableStatement(AngularParser.VariableStatementContext ctx) {

        VariableStatement variableStatement = new VariableStatement();
        for (int i = 0; i < ctx.variableDeclaration().size(); i++) {
            if (ctx.variableDeclaration(i) != null) {
                variableStatement.getVariableDeclarations().add(visitVariableDeclaration(ctx.variableDeclaration(i)));
            }
        }
        return variableStatement;

    }
    @Override
    public VariableDeclaration visitVariableDeclaration(AngularParser.VariableDeclarationContext ctx) {
        VariableDeclaration variableDeclaration = new VariableDeclaration();
        if (ctx.assignable() != null) {
            variableDeclaration.setAssignable(visitAssignable(ctx.assignable()));
        }
        if (ctx.singleExpression() != null) {
            variableDeclaration.setExp(visitSingleExpression(ctx.singleExpression()));
        }
        return variableDeclaration;
    }
    @Override
    public Assignable visitAssignable(AngularParser.AssignableContext ctx) {
        Assignable assignable = new Assignable();


        if (ctx.arrayLiteral() != null) {

            assignable.setArrayLiteral(visitArrayLiteral(ctx.arrayLiteral()));

        } else if (ctx.IDENTIFIER() != null) {
            assignable.setName(ctx.IDENTIFIER().getText());
            Row row1 = new Row();
            row1.setType("NameOfVar");
            row1.setValue(assignable.getName());
            this.globalsymbolTable.getRows().add(row1);

        }
        if (ctx.arrayLiteral() != null) {

            assignable.setArrayLiteral(visitArrayLiteral(ctx.arrayLiteral()));

        } else if (ctx.IDENTIFIER() != null) {
            assignable.setName(ctx.IDENTIFIER().getText());


        }

        return assignable;
    }
    @Override
    public ExpressionStatement visitExpressionStatement(AngularParser.ExpressionStatementContext ctx) {
        ExpressionStatement expressionStatement= new ExpressionStatement();
        for (int i = 0; i < ctx.singleExpression().size(); i++) {
            if (ctx.singleExpression(i) != null) {
                expressionStatement.getExpressions().add(visitSingleExpression(ctx.singleExpression(i)));
            }
        }
        return expressionStatement;

    }
    @Override
    public Export visitExportStatement(AngularParser.ExportStatementContext ctx) {
        Export export = new Export();
        if (ctx.IDENTIFIER() != null) {
            export.setIdentifier(ctx.IDENTIFIER().getText());
        }
        return export;
    }

    @Override
    public ClassDeclaration visitClassDeclaration(AngularParser.ClassDeclarationContext ctx) {
        ClassDeclaration classDeclaration=new ClassDeclaration();
        if(ctx.IDENTIFIER()!=null) {
            classDeclaration.setClassName(ctx.IDENTIFIER().getText());
            Row row = new Row();
            row.setType("ClassName ");
            row.setValue(classDeclaration.getClassName());
            this.globalsymbolTable.getRows().add(row);
        }
        if(ctx.classBody()!=null){
            classDeclaration.setClassBody(visitClassBody(ctx.classBody()));
        }
        return classDeclaration;
    }

    @Override
    public ClassBody visitClassBody(AngularParser.ClassBodyContext ctx) {
        ClassBody classBody=new ClassBody();

        for (int i=0;i<ctx.singleExpression().size();i++) {
            if (ctx.singleExpression(i) != null) {
                classBody.getExpressions().add(visitSingleExpression(ctx.singleExpression(i)));
            }
        }

        return classBody;
    }




    @Override
    public MustachExpression visitMustacheExpression(AngularParser.MustacheExpressionContext ctx) {
        MustachExpression mustachExpression = new MustachExpression();

        for (int i = 0; i < ctx.singleExpression().size(); i++) {
            if (ctx.singleExpression(i) != null) {
                Expression expr = visitSingleExpression(ctx.singleExpression(i));
                mustachExpression.getExpContent().add(expr);
            }
        }

        for (Expression expr : mustachExpression.getExpContent()) {
            String varName = expr.getIdentifier();

            if (varName != null) {

                if (!globalsymbolTable.contains(varName)) {
                    int line = ctx.getStart().getLine();  //////  /////////////////////// رقم السطر في الملف


                    UndefinedVariableError error = new UndefinedVariableError(varName, line);


                    semanticErrors.add(error);
                }
            }
        }

        return mustachExpression;
    }


    /*
    @Override
    public MustachExpression visitMustacheExpression(AngularParser.MustacheExpressionContext ctx) {
        MustachExpression mustachExpression=new MustachExpression();
        for (int i = 0; i < ctx.singleExpression().size(); i++) {
            if (ctx.singleExpression(i) != null) {

                mustachExpression.getExpContent().add(visitSingleExpression(ctx.singleExpression(i)));
            }
        }
        return mustachExpression;
    }
*/
    @Override
    public Selector visitSelectorDeclaration(AngularParser.SelectorDeclarationContext ctx) {
        Selector selector=new Selector();
        if(ctx.COLON()!=null){
            selector.setColon(ctx.COLON().getText());
        }
        if(ctx.SELECTOR()!=null){
            selector.setSelector(ctx.SELECTOR().getText());
        }
        if(ctx.STRINGLITERAL()!=null){
            selector.setApp_root(ctx.STRINGLITERAL().getText());
        }
        return selector;
    }

    @Override
    public Standalone visitStandaloneDeclaration(AngularParser.StandaloneDeclarationContext ctx) {
        Standalone standalone=new Standalone();
        if(ctx.STANDALONE()!=null){
            standalone.setStandalone(ctx.STANDALONE().getText());
        }
        if(ctx.COLON()!=null){
            standalone.setColon(ctx.COLON().getText());
        }
        if(ctx.BOOLEANLITERAL()!=null){
            String booleanText = ctx.BOOLEANLITERAL().getText();
            boolean booleanValue = Boolean.parseBoolean(booleanText);
            standalone.setBooleanvalue(booleanValue);
        }
        return standalone;
    }

    @Override
    public Imports visitImportsDeclaration(AngularParser.ImportsDeclarationContext ctx) {
        Imports imports=new Imports();
        if(ctx.COLON()!=null){
            imports.setColon(ctx.COLON().getText());
        }
        if(ctx.IMPORTS()!=null){
            imports.setImports(ctx.IMPORTS().getText());
        }
        if(ctx.arrayLiteral()!=null){
            imports.setArrayLiteral(visitArrayLiteral(ctx.arrayLiteral()));
        }
        return imports;
    }

    @Override
    public Styles visitStylesDeclaration(AngularParser.StylesDeclarationContext ctx) {
        Styles styles=new Styles();
        if(ctx.STYLES()!=null){
            styles.setStyle(ctx.STYLES().getText());
        }
        if(ctx.COLON()!=null){
            styles.setColon(ctx.COLON().getText());
        }
        if(ctx.arrayLiteral()!=null){
            styles.setArrayLiteral(visitArrayLiteral(ctx.arrayLiteral()));
        }
        return styles;
    }

    @Override
    public StyleContent visitSingleExpressionCss(AngularParser.SingleExpressionCssContext ctx) {
        StyleContent styleContent=new StyleContent();

        if(ctx.IDENTIFIER()!=null){

            styleContent.setClassName(ctx.IDENTIFIER().getText());
        }
        if(ctx.objectLiteral()!=null){
            styleContent.setObjectLiteral(visitObjectLiteral(ctx.objectLiteral()));
        }
        return styleContent;
    }
    @Override
    public forStatement visitForstatment(AngularParser.ForstatmentContext ctx) {
        forStatement fs = new forStatement();
        if (ctx.variableStatement() != null) {
            fs.setVariableStatement(visitVariableStatement(ctx.variableStatement()));
        }
        if (ctx.singleExpression(1) != null) {
            fs.setIncrement(visitSingleExpression(ctx.singleExpression(1)));
        }
        if (ctx.singleExpression(2) != null) {
            fs.setBody(visitSingleExpression(ctx.singleExpression(2)));
        }
        return fs;
    }

    @Override
    public IndexArray visitIndexarray(AngularParser.IndexarrayContext ctx) {
        IndexArray indexArray=new IndexArray();
        if(ctx.IDENTIFIER()!=null){
            indexArray.setIdentifier(ctx.IDENTIFIER().getText());
        }
        if(ctx.DECIMALLITERAL()!=null){
            indexArray.setIndex(ctx.DECIMALLITERAL().getChildCount());
        }
        return  indexArray;
    }
    /// ///////////////////////////////////////////////////////////
    public void printSemanticErrors() {
        if (semanticErrors.isEmpty()) {
            System.out.println("No semantic errors found.");
        } else {
            System.out.println("Semantic Errors:");
            System.out.println("=====================================\n\n ");
            for (SemanticError error : semanticErrors) {
                error.print();
                System.out.println("-------------------------------------\n\n");
            }
        }
    }

    // هنا يمكن إضافة المزيد من الـ visit methods الأخرى حسب حاجتك



    public void fillInitialSymbolTable() {
        // تعبئة المتغيرات
        Row r1 = new Row();
        r1.setType("variable");
        r1.setValue("products");
        globalsymbolTable.getRows().add(r1);

        Row r2 = new Row();
        r2.setType("variable");
        r2.setValue("selectedProduct");
        globalsymbolTable.getRows().add(r2);

        // تعبئة loop variable من *ngFor
        Row r3 = new Row();
        r3.setType("loopVar");
        r3.setValue("product");
        globalsymbolTable.getRows().add(r3);

        // خصائص المنتج
        Row r4 = new Row();
        r4.setType("property");
        r4.setValue("product.image");
        globalsymbolTable.getRows().add(r4);

        Row r5 = new Row();
        r5.setType("property");
        r5.setValue("selectedProduct.name");
        globalsymbolTable.getRows().add(r5);

        Row r6 = new Row();
        r6.setType("property");
        r6.setValue("selectedProduct.details");
        globalsymbolTable.getRows().add(r6);

        // ميثود
        Row r7 = new Row();
        r7.setType("method");
        r7.setValue("selectProduct");
        globalsymbolTable.getRows().add(r7);

        // التوجيهات الهيكلية
        Row r8 = new Row();
        r8.setType("structural-directive");
        r8.setValue("*ngFor");
        globalsymbolTable.getRows().add(r8);

        Row r9 = new Row();
        r9.setType("structural-directive");
        r9.setValue("*ngIf");
        globalsymbolTable.getRows().add(r9);



    }
}




