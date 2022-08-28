package com.alvaria.workordermanager.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * WorkOrderResponse model for Work-Orders
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Getter
@Setter
public class WorkOrderResponse {

	private Long id;
	private LocalDateTime time;
	private String message;
	private Integer position;
	private Long waitingTime;

}
