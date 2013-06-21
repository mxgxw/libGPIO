package co.teubi.raspberrypi.sshtool;

import com.jcraft.jsch.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import co.teubi.abstractions.Expectation;
import co.teubi.abstractions.Expectator;
import co.teubi.abstractions.io.InputStreamExpectations;



public class SSHClient implements Runnable,Expectator {
	

	private Timer watchdog;

	private JSch jsch;
	private ChannelShell shell;
	private Session ses;

	private BufferedWriter bw;
	private InputStreamExpectations sshInputStream;
	
	private String host;
	private String username;
	private String password;
	
	private ArrayList<Expectation> expectations;
	private ArrayList<String> commands;
	
	private int watchdogTimeout;

	public void queueCommand(String command) {
		commands.add(command);
	}

	private void startWatchdog(int timer) {
		if (watchdog != null) {
			watchdog.cancel();
		}
		final SSHClient self = this;
		watchdog = new Timer();
		watchdog.schedule((new TimerTask() {

			public void run() {
				self.sshInputStream.getExpectator().onFailed();
			}

		}), timer);
	}
	
	public void setWatchdogTimeout(int time) {
		this.watchdogTimeout = time;
	}

	public SSHClient(String host,String username,String password) {
		this.host = host;
		this.username = username;
		this.password = password;
		watchdog = new Timer();
		jsch = new JSch();
		commands = new ArrayList<String>();
		this.expectator = new Expectator() {
			@Override
			public void fulfill(Expectation exp) {
			}

			@Override
			public void onFailed() {
			}

		};
	}
	
	private Expectator expectator;

	public void setExpectator(Expectator exp) {
		this.expectator = exp;
	}

	public Expectator getExpectator() {
		return this.expectator;
	}

	public ArrayList<Expectation> getExpectations() {
		return this.sshInputStream.getExpectations();
	}
	
	public void setExpectations(ArrayList<Expectation> exp) {
		this.expectations = exp;
	}
	
	public void cancel() {
		if(shell!=null) {
			shell.disconnect();
		}
		if(ses!=null) {
			ses.disconnect();
		}
	}

	@Override
	public void run() {
		try {
			JSch.setConfig("StrictHostKeyChecking", "no");
			ses = jsch.getSession(this.username, this.host);
			ses.setPassword(this.password);

			ses.connect();

			// Trying
			shell = (ChannelShell) ses.openChannel("shell");

			PipedInputStream pin = new PipedInputStream();
			PipedOutputStream pout = new PipedOutputStream(pin);

			PipedOutputStream pout2 = new PipedOutputStream();
			PipedInputStream pin2 = new PipedInputStream(pout2);

			shell.setInputStream(pin2);
			shell.setOutputStream(pout);

			sshInputStream = new InputStreamExpectations(pin);
			
			sshInputStream.setExpectator(this);
			sshInputStream.setExpectations(this.expectations);
			
			Thread t = new Thread(sshInputStream);
			t.start();

			bw = new BufferedWriter(new OutputStreamWriter(pout2));

			shell.connect();
			
			this.startWatchdog(this.watchdogTimeout);
			
			for(int i=0;i<commands.size();i++) {
				try {
					bw.append(commands.get(i));
					bw.newLine();
					bw.flush();
				} catch (IOException e) {
					sshInputStream.getExpectator().onFailed();
				}
			}
			

			t.join(); // Wait reader thread for finish
			// Cancel watchdog
			
		} catch (JSchException e) {
			System.out.println(e.getMessage());
			onFailed();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			onFailed();
		} catch (InterruptedException e) {
			System.out.println(e.getMessage());
			onFailed();
		}
		
	}

	@Override
	public void fulfill(Expectation exp) {
		if(this.expectator!=null) {
			this.expectator.fulfill(exp);
		}
		if(this.expectations.indexOf(exp)==(this.expectations.size()-1)) {
			if(this.watchdog!=null) {
				this.watchdog.cancel();
			}
			ses.disconnect();
		}
	}

	@Override
	public void onFailed() {
		// TODO Auto-generated method stub
		if(ses!=null) {
			ses.disconnect();
		
		}
		if(this.expectator!=null) {
			this.expectator.onFailed();
		}
	}

}
