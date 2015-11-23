package bcoop.blocktracker.history;

import bcoop.block.HeaderBlock;

public class HeaderBlockHistory extends BaseHistory<HeaderBlock> {
	private static final long serialVersionUID = 7359564854199067723L;

	public HeaderBlockHistory(int maxHistory, DiscardEventHandlerIf<HeaderBlock> discardHandler) {
		super(maxHistory, discardHandler);
	}
}
