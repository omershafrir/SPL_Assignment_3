package bgu.spl.net.info.impl.newsfeed;

import bgu.spl.net.info.impl.rci.Command;
import java.io.Serializable;

public class FetchNewsCommand implements Command<NewsFeed> {

    private String channel;

    public FetchNewsCommand(String channel) {
        this.channel = channel;
    }

    @Override
    public Serializable execute(NewsFeed feed) {
        return feed.fetch(channel);
    }

}
