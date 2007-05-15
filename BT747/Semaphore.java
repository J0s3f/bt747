/*
 * Created on 13 mai 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

/**
 * @author Mario De Weerd
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Semaphore 
{
  private int value;

  public Semaphore( int value ) {
  	this.value = value;
  }

  public synchronized void down() {
    --value;
    if (value < 0) {
      try {	wait(); } catch( Exception e ) {}
    }
  }

  public synchronized void up() {
    ++value;
    notify();
  }
}
