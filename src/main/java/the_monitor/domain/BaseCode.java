package the_monitor.domain;

import the_monitor.application.dto.ReasonDto;

public interface BaseCode {

    public ReasonDto getReason();

    public ReasonDto getReasonHttpStatus();

}