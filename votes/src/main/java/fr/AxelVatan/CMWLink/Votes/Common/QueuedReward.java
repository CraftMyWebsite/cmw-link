package fr.AxelVatan.CMWLink.Votes.Common;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class QueuedReward {

	private @Getter String uuid;
	private @Getter @Setter List<String> cmds;
}
