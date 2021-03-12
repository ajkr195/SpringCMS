package com.spring.boot.rocks.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DBFilesInfo {
	private String filename, fileuuid, filetype, url, createdBy, createdDate, lastModifiedBy, lastModifiedDate;
}
