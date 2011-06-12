package de.k621.avr.gps.converter;

/**
 * Ein Interface, dass Threads die Anzeige ihres Fortschrittes erlauben soll
 *
 * @author Martin Matysiak
 */
public interface KProgressViewer {
  /**
   * Zeigt den Gegebenen Text auf der Oberfläche an
   *
   * @param pText Der anzuzeigende Text
   */
  public void updateText(String pText);

  /**
   * Aktualisiert die Position des Fortschrittbalkens
   *
   * @param pValue Die neue Position
   */
  public void updateBar(int pValue);

  /**
   * Erhöht die Position des Fortschrittbalkens um 1
   */
  public void incrementBar();

  /**
   * Setzt den Maximalwert des Fortschrittbalkens
   *
   * @param pValue Der neue Maximalwert
   */
  public void setBarMax(int pValue);
}
