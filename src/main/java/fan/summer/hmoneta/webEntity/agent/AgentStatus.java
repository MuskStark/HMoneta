package fan.summer.hmoneta.webEntity.agent;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AgentStatus {
    private int code;
    private String message;
    private boolean status;
}