package im.years.recyclerviewwrappersample;

import java.util.Date;

/**
 * Created by alvinzeng on 19/10/2016.
 */

public class ContentMock {
    public String title;
    public String content;
    public Date time = new Date();

    public ContentMock(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
