package fr.AxelVatan.CMWLink.Common.WebServer;

import express.utils.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class Result {

	private @Getter Status code;
	private @Getter String result;
}
