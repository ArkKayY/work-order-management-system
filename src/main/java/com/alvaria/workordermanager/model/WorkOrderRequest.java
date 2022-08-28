package com.alvaria.workordermanager.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Request model for Work-Orders
 */
@Getter
@Setter
@AllArgsConstructor
public class WorkOrderRequest {

	private Long id;
	private LocalDateTime time;

}
