/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timeclock;
import java.awt.*;
import java.awt.print.*;
// this was an example on the internet that was copied and tweaked.
/**
 * This wrapper class encapsulates a Component and allows it to be printed
 * using the Java 2 printing API.
 */
public class MyPrintable implements Printable 
{
    // The component to be printed.
    Component c;

    /** Create a PrintableComponent wrapper around a Component 
     * 
     * @param c printable component
     */
    public MyPrintable(Component c) { this.c = c; }

    /** 
     * This method is not part of the Printable interface.  It is a method
     * that sets up the PrinterJob and initiates the printing.
     * @throws PrinterException 
     */
     
   
    public void print() throws PrinterException 
    {
          // Get the PrinterJob object
          PrinterJob job = PrinterJob.getPrinterJob();
          // Get the default page format, then allow the user to modify it
          PageFormat format = job.pageDialog(job.defaultPage());
          // Tell the PrinterJob what to print
          job.setPrintable(this, format);
          // Ask the user to confirm, and then begin the printing process
          if (job.printDialog()) 
            job.print();
    }

    /**
     * This is the "callback" method that the PrinterJob will invoke.
     * This method is defined by the Printable interface.
     */
    public int print(Graphics g, PageFormat format, int pagenum) 
    {
          // The PrinterJob will keep trying to print pages until we return
          // this value to tell it that it has reached the end.
          if (pagenum > 0) 
            return Printable.NO_SUCH_PAGE;

          // We're passed a Graphics object, but it can always be cast to Graphics2D
          Graphics2D g2 = (Graphics2D) g;

          // Use the top and left margins specified in the PageFormat Note
          // that the PageFormat methods are poorly named.  They specify
          // margins, not the actual imageable area of the printer.
          g2.translate(format.getImageableX(), format.getImageableY());
          // scale the jpanel to the page

          double dw = format.getImageableWidth();
          double dh = format.getImageableHeight();
          Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
          // the 1.4 at the end is manipulatable
          double xScale = (dw / screenSize.width) * 1.4;
          double yScale = (dh / screenSize.height) * 1.4;
          double scale = Math.min(xScale,yScale);

          // center the chart on the page
          double tx = 0.0;
          double ty = 0.0;
          if (xScale > scale)
          {
              tx = 0.5*(xScale-scale)*screenSize.width;
          }
          else
                 {
              ty = 0.2*(yScale-scale)*screenSize.height;
          }
          g2.translate(tx, ty);
          g2.scale(scale, scale);
          // Tell the component to draw itself to the printer by passing in 
          // the Graphics2D object.  This will not work well if the component
          // has double-buffering enabled.
          c.paint(g2);

          // Return this constant to tell the PrinterJob that we printed the page.
          return Printable.PAGE_EXISTS;
    }// end print
}// end myprintable class
