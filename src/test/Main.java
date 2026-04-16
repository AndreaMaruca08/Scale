import core.ScaleUIApplication;
import test.TestPage;

import java.awt.*;

void main() {
    ScaleUIApplication app = new ScaleUIApplication("FRAMEWORK TEST");

    TestPage page = new TestPage(app);
    app.addPage(page);

    app.changePage(page.getPageName());
    app.setVisible(true);
}
