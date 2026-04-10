import core.ScaleUIApplication;
import test.TestPage;

void main() {
    ScaleUIApplication app = new ScaleUIApplication("FRAMEWORK TEST");

    TestPage page = new TestPage(app);
    app.addPage(page);

    app.changePage("TestPage");
    app.setVisible(true);
}
