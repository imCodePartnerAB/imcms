package formbeans;

public class ImageBrowseBean {
    private String metaId;
    private String imageNumber;
    private String label;
    private String imageList;
    private String dirListPreset;
    private String folders;
    private String imagePreview;
    private String options;
    private String nextButton;
    private String previousButton;
    private String startNumber;
    private String stopNumber;
    private String maxNumber;

    private String caller;

    public String getMetaId() {
        return metaId;
    }

    public String getImageNumber() {
        return imageNumber;
    }

    public String getLabel() {
        return label;
    }

    public String getImageList() {
        return imageList;
    }

    public String getDirListPreset() {
        return dirListPreset;
    }

    public void setMetaId(String metaId) {
        this.metaId = metaId;
    }

    public void setImageNumber(String imageNumber) {
        this.imageNumber = imageNumber;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setImageList(String imageList) {
        this.imageList = imageList;
    }

    public void setDirListPreset(String dirListPreset) {
        this.dirListPreset = dirListPreset;
    }

    public String getFolders() {
        return folders;
    }

    public void setFolders(String folders) {
        this.folders = folders;
    }

    public String getImagePreview() {
        return imagePreview;
    }

    public void setImagePreview(String imagePreview) {
        this.imagePreview = imagePreview;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public String getNextButton() {
        return nextButton;
    }

    public void setNextButton(String nextButton) {
        this.nextButton = nextButton;
    }

    public String getPreviousButton() {
        return previousButton;
    }

    public void setPreviousButton(String previousButton) {
        this.previousButton = previousButton;
    }

    public String getStartNumber() {
        return startNumber;
    }

    public void setStartNumber(String startNumber) {
        this.startNumber = startNumber;
    }

    public String getStopNumber() {
        return stopNumber;
    }

    public String getMaxNumber() {
        return maxNumber;
    }

    public void setStopNumber(String stopNumber) {
        this.stopNumber = stopNumber;
    }

    public void setMaxNumber(String maxNumber) {
        this.maxNumber = maxNumber;
    }

    public void setCaller(String caller) {
        this.caller = caller;
    }

    public String getCaller() {
        return caller;
    }
}
