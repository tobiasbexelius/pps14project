// Stub class generated by rmic, do not edit.
// Contents subject to change without notice.

package elevator.rmi;

public final class LinkButton_Stub
    extends java.rmi.server.RemoteStub
    implements elevator.rmi.RemoteActionListener, java.rmi.Remote
{
    private static final long serialVersionUID = 2;
    
    private static java.lang.reflect.Method $method_actionPerformed_0;
    
    static {
	try {
	    $method_actionPerformed_0 = elevator.rmi.RemoteActionListener.class.getMethod("actionPerformed", new java.lang.Class[] {java.awt.event.ActionEvent.class});
	} catch (java.lang.NoSuchMethodException e) {
	    throw new java.lang.NoSuchMethodError(
		"stub class initialization failed");
	}
    }
    
    // constructors
    public LinkButton_Stub(java.rmi.server.RemoteRef ref) {
	super(ref);
    }
    
    // methods from remote interfaces
    
    // implementation of actionPerformed(ActionEvent)
    public void actionPerformed(java.awt.event.ActionEvent $param_ActionEvent_1)
	throws java.rmi.RemoteException
    {
	try {
	    ref.invoke(this, $method_actionPerformed_0, new java.lang.Object[] {$param_ActionEvent_1}, -3052806174630731285L);
	} catch (java.lang.RuntimeException e) {
	    throw e;
	} catch (java.rmi.RemoteException e) {
	    throw e;
	} catch (java.lang.Exception e) {
	    throw new java.rmi.UnexpectedException("undeclared checked exception", e);
	}
    }
}
