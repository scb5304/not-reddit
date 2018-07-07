package com.stevenbrown.notreddit.ui.postdetail;

class CommentClick {
    private Integer currentSelectedIndex;
    private Integer newSelectedIndex;

    CommentClick(Integer currentSelectedIndex, Integer newSelectedIndex) {
        this.currentSelectedIndex = currentSelectedIndex;
        this.newSelectedIndex = newSelectedIndex;
    }

    Integer getCurrentSelectedIndex() {
        return currentSelectedIndex;
    }

    Integer getNewSelectedIndex() {
        return newSelectedIndex;
    }
}