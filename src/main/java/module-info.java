module mobile.screen.tool.mobiletool {
    requires javafx.controls;
    requires javafx.fxml;


    opens mobile.screen.tool to javafx.fxml;
    exports mobile.screen.tool;
}