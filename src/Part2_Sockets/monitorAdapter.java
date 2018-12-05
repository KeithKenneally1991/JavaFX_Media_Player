package Part2_Sockets;

public class monitorAdapter implements listListener{

private monitorFolder monitor;
	
	public monitorAdapter(monitorFolder mon){
		this.monitor = mon;
	}

	
	@Override
	public boolean checkForChange() {
		// TODO Auto-generated method stub
		return monitor.checkForChange();
	}
	

	
}
