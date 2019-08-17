package cn.sa.demo.entity;

/**
 * Created by yzk on 2019/5/16
 */

public class ViewNodeEntity {
    private String viewPath;

    public String getViewContent() {
        return viewContent;
    }

    public String getViewPage() {
        return viewPage;
    }

    private String viewContent;
    private int viewPosition;
    private String viewPage;
    private int mHashCode = 0;


    public ViewNodeEntity() {

    }

    public ViewNodeEntity(String viewPath) {
        this.viewPath = viewPath;
    }

    public ViewNodeEntity(String viewPage, String viewPath, String viewContent) {
        this.viewPage = viewPage;
        this.viewPath = viewPath;
        this.viewContent = viewContent;
    }

    public int getViewPosition() {
        return viewPosition;
    }

    public ViewNodeEntity setViewPage(String viewPage) {
        this.viewPage = viewPage;
        return this;
    }

    public ViewNodeEntity setViewPosition(int viewPosition) {
        this.viewPosition = viewPosition;
        return this;
    }


    public String getViewPath() {
        return viewPath;
    }

    public void setViewPath(String viewPath) {
        this.viewPath = this.viewPath + "\\/" + viewPath;
    }


    public int hashCode() {
        if (this.mHashCode == 0) {
            int hashCode;
            if (this.viewContent != null) {
                hashCode = this.viewContent.hashCode();
            } else {
                hashCode = 0;
            }
            int i2 = (hashCode + 300) * 21;
            if (this.viewPath != null) {
                hashCode = this.viewPath.hashCode();
            } else {
                hashCode = 0;
            }
            mHashCode = (i2 + hashCode) * 18;

        }
        return this.mHashCode;
    }

    @Override
    public String toString() {
        return "    \n{" +
                "  \n       \"viewPath\":\"" + viewPath + "\"" +
                ", \n       \"viewContent\":\"" + viewContent + "\"" +
                ", \n       \"viewPage\":\"" + viewPage + "\"" +
                ", \n       \"mHashCode\":" + mHashCode +
                "   \n}";
    }
}
