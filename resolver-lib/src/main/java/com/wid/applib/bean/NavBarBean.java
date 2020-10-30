package com.wid.applib.bean;

/**
 * author:Marcus
 * create on:2019/6/12 10:47
 * description
 */
public class NavBarBean {

    /**
     * common : {"rightText":"导航","x":41,"name":"","y":179,"lock":false,"tag":"","title":"导航栏","backText":"返回","height":50,"isHidden":false}
     * waitToCopy : false
     * name : 导航栏
     * style : {"rightIcon":false,"rightTextColor":"#007aff","backIcon":false,"rightImgUrl":"","backTextColor":"#007aff","rightIconFileName":"","backImgUrl":"","bgColor":"#f7f7f8","titleColor":"#000000","titleFontSize":20,"rightIconParam":"50_50_50_50","backIconFileName":"","backIconParam":"50_50_50_50"}
     * type : navbar
     * cid : 2a4u27erf5g
     */

    private CommonBean common;
    private boolean waitToCopy;
    private String name;
    private StyleBean style;
    private String type;
    private String cid;

    public CommonBean getCommon() {
        return common;
    }

    public void setCommon(CommonBean common) {
        this.common = common;
    }

    public boolean isWaitToCopy() {
        return waitToCopy;
    }

    public void setWaitToCopy(boolean waitToCopy) {
        this.waitToCopy = waitToCopy;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public StyleBean getStyle() {
        return style;
    }

    public void setStyle(StyleBean style) {
        this.style = style;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public static class CommonBean {
        /**
         * rightText : 导航
         * x : 41
         * name :
         * y : 179
         * lock : false
         * tag :
         * title : 导航栏
         * backText : 返回
         * height : 50
         * isHidden : false
         */

        private String rightText;
        private int x;
        private String name;
        private int y;
        private boolean lock;
        private String tag;
        private String title;
        private String backText;
        private int height;
        private boolean isHidden;

        public String getRightText() {
            return rightText;
        }

        public void setRightText(String rightText) {
            this.rightText = rightText;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public boolean isLock() {
            return lock;
        }

        public void setLock(boolean lock) {
            this.lock = lock;
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getBackText() {
            return backText;
        }

        public void setBackText(String backText) {
            this.backText = backText;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public boolean isIsHidden() {
            return isHidden;
        }

        public void setIsHidden(boolean isHidden) {
            this.isHidden = isHidden;
        }
    }

    public static class StyleBean {
        /**
         * rightIcon : false
         * rightTextColor : #007aff
         * backIcon : false
         * rightImgUrl :
         * backTextColor : #007aff
         * rightIconFileName :
         * backImgUrl :
         * bgColor : #f7f7f8
         * titleColor : #000000
         * titleFontSize : 20
         * rightIconParam : 50_50_50_50
         * backIconFileName :
         * backIconParam : 50_50_50_50
         */

        private boolean rightIcon;
        private String rightTextColor;
        private boolean backIcon;
        private String rightImgUrl;
        private String backTextColor;
        private String rightIconFileName;
        private String backImgUrl;
        private String bgColor;
        private String titleColor;
        private int titleFontSize;
        private String rightIconParam;
        private String backIconFileName;
        private String backIconParam;

        public boolean isRightIcon() {
            return rightIcon;
        }

        public void setRightIcon(boolean rightIcon) {
            this.rightIcon = rightIcon;
        }

        public String getRightTextColor() {
            return rightTextColor;
        }

        public void setRightTextColor(String rightTextColor) {
            this.rightTextColor = rightTextColor;
        }

        public boolean isBackIcon() {
            return backIcon;
        }

        public void setBackIcon(boolean backIcon) {
            this.backIcon = backIcon;
        }

        public String getRightImgUrl() {
            return rightImgUrl;
        }

        public void setRightImgUrl(String rightImgUrl) {
            this.rightImgUrl = rightImgUrl;
        }

        public String getBackTextColor() {
            return backTextColor;
        }

        public void setBackTextColor(String backTextColor) {
            this.backTextColor = backTextColor;
        }

        public String getRightIconFileName() {
            return rightIconFileName;
        }

        public void setRightIconFileName(String rightIconFileName) {
            this.rightIconFileName = rightIconFileName;
        }

        public String getBackImgUrl() {
            return backImgUrl;
        }

        public void setBackImgUrl(String backImgUrl) {
            this.backImgUrl = backImgUrl;
        }

        public String getBgColor() {
            return bgColor;
        }

        public void setBgColor(String bgColor) {
            this.bgColor = bgColor;
        }

        public String getTitleColor() {
            return titleColor;
        }

        public void setTitleColor(String titleColor) {
            this.titleColor = titleColor;
        }

        public int getTitleFontSize() {
            return titleFontSize;
        }

        public void setTitleFontSize(int titleFontSize) {
            this.titleFontSize = titleFontSize;
        }

        public String getRightIconParam() {
            return rightIconParam;
        }

        public void setRightIconParam(String rightIconParam) {
            this.rightIconParam = rightIconParam;
        }

        public String getBackIconFileName() {
            return backIconFileName;
        }

        public void setBackIconFileName(String backIconFileName) {
            this.backIconFileName = backIconFileName;
        }

        public String getBackIconParam() {
            return backIconParam;
        }

        public void setBackIconParam(String backIconParam) {
            this.backIconParam = backIconParam;
        }
    }
}
