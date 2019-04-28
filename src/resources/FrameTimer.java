package resources;

public class FrameTimer {

	private int delay, limit;
	private boolean isDone = false, isPaused;
	
	public FrameTimer(int limit, boolean isDone) {
		this.limit = limit;
		if(isDone) {
			delay = limit;
		}
	}
	
	public void update() {
		if(!isPaused) {
			if(delay < limit) delay++;
			else isDone = true;
		}
	}
	
	public boolean isDone() {
		return isDone;
	}
	public boolean isPaused() {
		return isPaused;
	}
	public int getFrameCount() {
		return delay;
	}
	public int getLimit() {
		return limit;
	}
	public void setLimit(int limit) {
		this.limit = limit;
	}
	public void resetTimer() {
		delay = 0;
		isDone = false;
	}
	public void completeTimer() {
		delay = limit;
	}
	
	public void pause() {
		isPaused = true;
	}
	public void start() {
		isPaused = false;
	}
}
