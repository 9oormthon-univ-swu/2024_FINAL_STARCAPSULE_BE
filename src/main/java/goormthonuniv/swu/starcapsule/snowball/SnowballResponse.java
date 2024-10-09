package goormthonuniv.swu.starcapsule.snowball;

import com.fasterxml.jackson.annotation.JsonProperty;
import goormthonuniv.swu.starcapsule.memory.Memory;
import lombok.Getter;

@Getter
public class SnowballResponse {
    private Long id;
    @JsonProperty("snowball_name")
    private String snowballName;

    @JsonProperty("snowball_link_id")
    private String snowballLinkId;
    @JsonProperty("shared_link")
    private String sharedLink;

    public SnowballResponse(Snowball snowball){
        this.id = snowball.getId();
        this.snowballName = snowball.getSnowballName();
        this.snowballLinkId = snowball.getSnowballLinkId();
        this.sharedLink = snowball.getSharedLink();
    }
}
