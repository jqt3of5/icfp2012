import java.util.*;

public class RandBoard{

	private static final int maxHeight = 300;
	private static final int maxWidth = 300;

	private static int width;
	private static int height;

	private static Random r = new Random();

	private static char[][] board;

	public static char[] getNewMap()
	{
		width = maxWidth;
		height = maxHeight;
		board = new int[width][height];
		fill();
	}

	public static char[] getNewMap(int width, int height)
	{
		this.width = width;
		this.height = height;
		board = new int[width][height];
		fill();
	}

	private static void fill()
	{
		for (int i = 0; i < width; i++)
		{	
			for(int j = 0; j < height; j++)
			{
				if((i == 0) || (i == width - 1) || (j == 0) || (j == height - 1))
				{
					board[i][j] = '#';
				}
				else
				{
					switch(r.nextInt(2))
					{
						case 0:
							board[i][j] = '.'; //earth
							break;
						case 1:
							board[i][j] = '*'; //rock
							break;
						case 2:
							board[i][j] = '\\'; //lambda
							break;
					}
				}
			}
		}

		for (int i = 0; i < width; i++)
		{
			for (int j = 0; j < height; j++)
			{
				
			}
		}
	}
}
