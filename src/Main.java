import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

public class Main {
	
	static final boolean debugmode = false;
	public static int clocks = 1;
	public static boolean printPerClock = true;
	static final boolean freePrecision = false;
	public static int nClocks = 1;
	
	static void debugprint(String s)
	{
		if(debugmode)
		{
			System.out.println(s);
		}
	}

	public static void main(String[] args) 
	{
		Scanner in = null;
		try {
			in = new Scanner(new FileReader(args[0]));
		} catch (FileNotFoundException e) {
			System.out.println("ELLOLE SIIIIIIIIII");
			return;
		}
		
		ValueMap vmap = new ValueMap(in.nextInt(), in.nextInt(), in.nextFloat());
		
		while(in.hasNext())
		{
			vmap.setValue(in.nextInt(), in.nextInt(), in.nextFloat());
		}
		
		in.close();
		in = new Scanner(System.in);
		String c = "";
		vmap.print();
		System.out.println("\tsomma: " + vmap.sumAll());
		System.out.println("\tmedia globale: " + vmap.globalAvg);
		System.out.print("-> ");
		c = in.next().trim();
		System.out.println();
		
		while(c.equals("y"))
		{
			for(int i=0; i< clocks; i++)
			{
				vmap.V3_updateStep1();
				vmap.V3_updateStep2();
				nClocks++;
				if(printPerClock) {
					System.out.println("clock: " + nClocks);
					vmap.print();
					System.out.println();
				}
			}
			if(!printPerClock)
			{
				System.out.println("clock: " + nClocks);
				vmap.print();
			}
			System.out.println("\tsomma: " + vmap.sumAll());
			System.out.print("-> ");
			c = in.next().trim();
			System.out.println();
		}
	}
}



