# WebSocketPlugin for Red5.

## what is this.
This is the plugin for red5 server to use websocket protocol.

## how to use this.
1st, put the webSocketPlugin.jar on red5/plugin/ directory.

2nd, update conf/red5.xml
after pluginlauncher bean. add webSocketTransport bean.

before..

    <bean id="pluginLauncher" class="org.red5.server.plugin.PluginLauncher" />

after..

    <bean id="pluginLauncher" class="org.red5.server.plugin.PluginLauncher" />
    <bean id="webSocketTransport" class="com.ttProject.red5.server.plugin.websocket.WebSocketTransport" init-method="start">
      <property name="port" value="8080"/>
    </bean>

this example try to setup the websocket server with 8080 port.

3rd, make red5 application program.

    package com.any.project;
    
    import org.red5.server.adapter.ApplicationAdapter;
    
    import com.ttProject.red5.server.plugin.websocket.IWebSocketDataListener;
    import com.ttProject.red5.server.plugin.websocket.WebSocketConnection;
    import com.ttProject.red5.server.plugin.websocket.WebSocketScopeManager;
    
    public class Application extends ApplicationAdapter implements IWebSocketDataListener{
    	public Application() {
    		WebSocketScopeManager wssm = new WebSocketScopeManager();
    		wssm.addPluginedApplication("test", this); // note:test is application name for websocket.
    	}
    
    	@Override
    	public void connect(WebSocketConnection conn) {
    		System.out.println("connect...");
    	}
    
    	@Override
    	public void leave(WebSocketConnection conn) {
    		System.out.println("leave");
    	}
    
    	@Override
    	public void receiveData(WebSocketConnection conn, Object data) {
    		// send the data to connection with same scope
    		try {
    			System.out.println(data);
    			conn.getScope().sendAll((String)data);
    		}
    		catch (Exception e) {
    			e.printStackTrace();
    		}
    	}
    }

last, compile this code and make your application for red5.

go to http://websocket.org/echo.html

connect with the address "ws://[yourserver address]/test"

and send any message

## checked with.
I checked this plugin on red5 server 1.0.0 RC2 $Rev: 4238 on iMac
