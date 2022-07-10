package fr.AxelVatan.CMWLink.Common.Config.WebServer;

import express.utils.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class Result {

	private @Getter Status code;
	private @Getter String result;
}
