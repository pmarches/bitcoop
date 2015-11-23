package bcoop.blocktracker.history;

public interface DiscardEventHandlerIf <T>{
	public void onBeforeBlockToRemove(T blockToRemove);
}
