package it.ocramsoft;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Scanner;

import javax.swing.JPanel;
import javax.swing.Timer;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.DynamicTimeSeriesCollection;
import org.jfree.data.time.Second;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

public class MBeanAttributePoller extends ApplicationFrame {

	private static final long serialVersionUID = 1L;

	protected JmxMBeanClientInterface attributeClient;

	int pollingInterval = 1000;

	private Timer timer;
	JFreeChart chart;
	
	private float maxVal=1000000;
	private int numberOfPoints = 100;
	protected  float minRange = 0;
	protected  float maxRange = 0;
	public boolean rangeActive = false;

	public boolean verbose = false;
	public boolean showChart = true;

	//Connection options
	protected String remoteHost;
	protected int remotePort;
	
	//Query options
	protected String queryObject;
	protected String queryAttribute;
	protected boolean isComposite;
	protected String queryCompositeKey;	
		
	public MBeanAttributePoller(String appName) {
		super(appName);

		final DynamicTimeSeriesCollection dataset = new DynamicTimeSeriesCollection(1, numberOfPoints, new Second());
		LocalDateTime lt = LocalDateTime.now();
		lt = lt.minusSeconds(numberOfPoints);
		dataset.setTimeBase( new Second(lt.getSecond(), lt.getMinute(),lt.getHour(),lt.getDayOfMonth(), lt.getMonthValue(), lt.getYear()));
		dataset.addSeries(new float[1], 0, "MBean value");

		chart = createChart(dataset);

		this.add(new ChartPanel(chart), BorderLayout.CENTER);
		JPanel btnPanel = new JPanel(new FlowLayout());
		this.add(btnPanel, BorderLayout.SOUTH);

		timer = new Timer(pollingInterval, new ActionListener() {

			float[] newData = new float[1];

			public void actionPerformed(ActionEvent e) {
				try {
					Object val = attributeClient.retrieveAttributeValue(queryObject,queryAttribute,isComposite,queryCompositeKey);
					
					if(verbose)
						System.out.println("Value for attribute queryAttribute is "+val);
					
					if(!showChart)
						return;
					
					Method method = val.getClass().getMethod("floatValue", null);
					
					//If the value cannot be cast to float no chart can be shown
					if(method == null)
					{
						MBeanAttributePoller.this.setVisible(false);
						showChart = false;
						System.err.println("The value of the MBean "+queryAttribute+ " attribute cannot be cast to float, cannot show it in a chart");
					}
					
					newData[0] = (Float) method.invoke(val);

					if(rangeActive &&(newData[0]>maxRange || newData[0]<minRange))
						System.out.println("WARNING! The value of the attribute "+queryAttribute+"="+newData[0]+" exceeded the range ["+minRange+":"+maxRange+"]");

					chart.setTitle("Current "+queryAttribute+" value :"+val);
					
				} catch (Exception e1) {
					e1.printStackTrace();
					return;
				}
				dataset.advanceTime();
				maxVal = newData[0] + newData[0]/2;
				chart.getXYPlot().getRangeAxis().setRange(0, maxVal);
				dataset.appendData(newData);
			}
		});
	}

	private JFreeChart createChart(final XYDataset dataset) {
		final JFreeChart result = ChartFactory.createTimeSeriesChart("MBean value chart", "hh:mm:ss", "MBean value", dataset, true,
				true, false);
		final XYPlot plot = result.getXYPlot();
		ValueAxis domain = plot.getDomainAxis();
		domain.setAutoRange(true);
		ValueAxis range = plot.getRangeAxis();
		range.setRange(0, maxVal);
		return result;
	}

	public void start() {
		timer.start();
	}

	public void stop() {
		timer.stop();
	}

	public static void main(String[] args) throws Exception {

		MBeanAttributePoller poller = new MBeanAttributePoller("MBean poller application");

		OptionParser parser = initParser();
		OptionSet options = parser.parse(args);

		if (!poller.readOptions(options, parser))
			return;

		JmxMBeanClientInterface attributeClient = new JmxMBeanClient(poller.remoteHost, poller.remotePort);
		
		attributeClient.openConnection();
		
		//Inject the initialized object
		poller.attributeClient = attributeClient;
		
		//Create main windows
		poller.pack();
        RefineryUtilities.centerFrameOnScreen(poller);
        poller.setVisible(true);
        
        //Start the main window
		poller.start();
		
		//Wait for the user to exit
		String code = "";
		Scanner keyboard ;
		
		do{
			keyboard = new Scanner(System.in);
			System.out.println("Enter q for exit");
			code = keyboard.next();
		} while(!code.equals("q"));
		

		keyboard.close();
		poller.stop();
		
		attributeClient.closeConnection();	
		
		System.exit(0);
	}

	private static OptionParser initParser() throws IOException {

		OptionParser parser = new OptionParser();
		parser.accepts("port").withRequiredArg().ofType(Integer.class).describedAs("Remote port").required();

		parser.accepts("host").withRequiredArg().ofType(String.class).describedAs("Remote host").required();

		parser.accepts("type").withRequiredArg().ofType(String.class).describedAs("The MBean type").required();

		parser.accepts("attribute").withRequiredArg().ofType(String.class).describedAs("The MBean attribute name")
				.required();

		parser.accepts("compositeVar").withRequiredArg().ofType(String.class)
				.describedAs("The composite subattribute name");

		parser.accepts("listenToAttribute").withRequiredArg().ofType(String.class)
				.describedAs("Receive notification from a published MBean");

		parser.accepts("pollingInterval").withRequiredArg().ofType(Integer.class)
				.describedAs("The interval for checking the option value in seconds");
				
		parser.accepts("verbose").representsNonOptions();
		
		parser.accepts("rangeValue").withRequiredArg().withValuesSeparatedBy(":").ofType(Integer.class)
		.describedAs("The range of valid values [minVal:maxVal], if the attribute exceeds the range an alert is printed");

		parser.accepts("help").forHelp();

		return parser;
	}

	private boolean readOptions(OptionSet options, OptionParser parser) throws Exception {

		if (options.has("help")) {
			parser.printHelpOn(System.out);
			return false;
		}

		if (options.has("listenToAttribute")) {
			throw new Exception("Not implemented yet!");
		}

		if (options.has("port")) {
			remotePort = (Integer)options.valueOf("port");
		}

		if (options.has("host")) {
			remoteHost = (String) options.valueOf("host");
		}

		if (options.has("type")) {
			queryObject = (String) options.valueOf("type");
		}

		if (options.has("attribute")) {
			queryAttribute = (String) options.valueOf("attribute");
		}

		if (options.has("compositeVar")) {
			isComposite = true;
			queryCompositeKey = (String) options.valueOf("compositeVar");
		}

		//TODO:Fixme has affect on the timer but not on the chart, fixme please!
		if (options.has("pollingInterval")) {
			pollingInterval = ((Integer) options.valueOf("pollingInterval")*1000);
		} else
			pollingInterval = 1000;
		
		if (options.has("rangeValue")) 
		{
			minRange = ((Integer) options.valuesOf("rangeValue").get(0)).floatValue();
			maxRange = ((Integer) options.valuesOf("rangeValue").get(1)).floatValue();
			rangeActive=true;
		}
		
		if (options.has("verbose")) {
			verbose = true;
		} else
			verbose = false;

		return true;
	}

}
