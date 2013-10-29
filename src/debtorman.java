import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.lang.*;

import java.net.*;
import java.awt.Graphics2D;
import java.util.logging.*;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import java.awt.geom.*;

enum SoundEffect {
	   CASH_RING("cashring.wav"),
	   CLIP_CLIP("clipclip.wav"),
	   DING("dddding.wav"),
	   DROPPING("dropping.wav"),
	   PLINK("plink.wav"),
	   SNIPPING("snipping.wav"),
	   TICK_TICK("ticktick.wav"),
	   WOO_WOO("woowoo.wav");   
	   
	   // Nested class for specifying volume
	   public static enum Volume {
	      MUTE, LOW, MEDIUM, HIGH
	   }
	   
	   public static Volume volume = Volume.LOW;
	   
	   // Each sound effect has its own clip, loaded with its own sound file.
	   private Clip clip;
	   
	   // Constructor to construct each element of the enum with its own sound file.
	   SoundEffect(String soundFileName) {
		   
	      try {
	         // Use URL (instead of File) to read from disk and JAR.
	         URL url = this.getClass().getClassLoader().getResource(soundFileName);
	         // Set up an audio input stream piped from the sound file.
	         AudioInputStream audioInputStream = null;
	         if(null == url)
	         {
	        	 File f = new File(soundFileName);
	        	 audioInputStream = AudioSystem.getAudioInputStream(f); 
	         }
	         else
	         {
	        	 audioInputStream = AudioSystem.getAudioInputStream(url); 
	         }
	         // Get a clip resource.
	         clip = AudioSystem.getClip();
	         // Open audio clip and load samples from the audio input stream.
	         clip.open(audioInputStream);
	         
	         
	      } catch (UnsupportedAudioFileException e) {
	         e.printStackTrace();
	      } catch (IOException e) {
	         e.printStackTrace();
	      } catch (LineUnavailableException e) {
	         e.printStackTrace();
	      }
	      catch (Exception e)
	      {
	    	  e.printStackTrace();
	      }
	      
	        	 
	   }
	   
	   // Play or Re-play the sound effect from the beginning, by rewinding.
	   public void play() {
	      if (volume != Volume.MUTE) {
	         if (clip.isRunning())
	            clip.stop();   // Stop the player if it is still running
	         clip.setFramePosition(0); // rewind to the beginning
	         clip.start();     // Start playing
	      }
	   }
	   public void playIfNotPlaying()
	   {
		   if (volume != Volume.MUTE) {
		         if (clip.isRunning())
		            return;   // Stop the player if it is still running
		         clip.setFramePosition(0); // rewind to the beginning
		         clip.start();     // Start playing
		      }
	   }
	   
	   // Optional static method to pre-load all the sound files.
	   static void init() {
	      values(); // calls the constructor for all the elements
	   }
	}

/************************************************************/
/*                    D E B T M A N   C L A S S             */
/************************************************************/

final class debtman
{

    public int xi,xl;
    public int yi,yl;

    int rowstart = 0;
    int plinkcountdown = 0;
    int lives;

    int anc;

    public Image [] left;
    public Image [] right;
    public Image [] up;
    public Image [] down;

    public Image disp;

    public int sx,sy;

    public int screenx,screeny;

    boolean moving;

    char dir;
    char nextdir;

    public boolean atePowerPellet;

    public int points;
    public int score;

    int [][]m;
    public int [][]p;

    public debtman(Image [] a,
		   Image [] b, 
		   Image [] c,
		   Image [] d,
		   int [][]mm,
		   int [][]pp)
    {
	int i,j;
	anc = 0;
	left = a;
	right = b;
	up = c;
	down = d;

	rowstart = 0;

	score = 0;

	sx = 4;
	sy = 13;
	moving = true;

	lives = 3;

	dir = 'L';

	atePowerPellet=false;

	xl = 8;
	xi = xl/2;

	yl = 8;
	yi = 0;

	disp = a[0];

	nextdir = ' ';

	points = 0;

	m=mm;
	p = null;
	setPellet(pp);
	/*	p=(int [][])pp.clone();*/
    }

    public void setMaze(int mm[][])
    {
	m = mm;
    }

    public void totalReset()
    {
	lives = 3;
	score = 0;
	resetAfterDying();
    }

    public void resetAfterDying()
    {
	rowstart = 0;
	xi = xl/2;
	yi = 0;

	dir = 'L';
	nextdir=' ';

	moving = true;

	sx = 4;
	sy = 13;

	disp = left[0];

    }

    public void setPellet(int [][]pp)
    {
	int i,j;
	p = new int[17][9];
	for(i=0;i<9;i++)
	    for(j=0;j<17;j++)
		p[j][i] = pp[j][i];

    }

    public boolean allEaten()
    {
	int i,j,k;
	k = 0;
	for(j=rowstart;j<17;j++)
	    {
	    for(i=0;i<9;i++)
		{
		    k += p[j][i];
		    if(k>0)
			{
			    return false;
			}
		}
	    //System.out.println("Increment the starting row!");
	      rowstart++;
	    }

	return true;
    }

    boolean goLeft()
    {
	switch(m[sy][sx])
	    {
	    case 11:
	    case 3:
	    case 4:
	    case 5:
	    case 7:
	    case 9:
	    case 10:
		return true;
	    default:
		return false;
	    }
    }

    boolean goRight()
    {
	switch(m[sy][sx])
	    {
	    case 11:
	    case 1:
	    case 2:
	    case 5:
	    case 7:
	    case 8:
	    case 9:
		return true;
	    default:
		return false;
	    }
    }

    boolean goUp()
    {
	switch(m[sy][sx])
	    {
	    case 11:
	    case 2:
	    case 3:
	    case 6:
	    case 7:
	    case 8:
	    case 10:
		return true;
	    default:
		return false;
	    }
    }

    boolean goDown()
    {
	switch(m[sy][sx])
	    {
	    case 11:
	    case 1:
	    case 4:
	    case 6:
	    case 8:
	    case 9:
	    case 10:
		return true;
	    default:
		return false;
	    }
    }

    boolean testNextMove()
    {
	switch(nextdir)
	    {
	    case 'U':
		return goUp();
	    case 'D':
		return goDown();
	    case 'L':
		return goLeft();
	    case 'R':
		return goRight();
	    }/*end switch*/

	return  false;
    }

    public void move()
    {

	char savedir = ' ';

	if(!moving)
	    return;


	if((xi!=0) && (yi!=0))
	    {
		xi = 0;
		yi = 0;
	    }

	if(xi == 0&&yi == 0)
	    {
		
		if(nextdir != ' ')
		    {
			savedir = dir;
			dir = nextdir;
			nextdir=' ';
			
			switch (dir)
			    {
			    case 'U':
				if(!goUp())
				    {
					nextdir = dir;
					dir = savedir;
				    }
				break;
			    case 'D':
				if(!goDown())
				    {
					nextdir = dir;
					dir = savedir;
				    }
				break;
			    case 'R':
				if(!goRight())
				    {
					nextdir = dir;
					dir = savedir;
				    }
				break;
			    case 'L':
				if(!goLeft())
				    {
					nextdir = dir;
					dir = savedir;
				    }
				break;
			    }/*end switch*/
		    }
	    }
	
	switch(dir)
	    {
	    case 'U':
		yi--;
		if(yi<0)
		    {

			if(goUp())
			    {
				sy--;
				yi = yl-1;
			    }
			else
			    {
				yi = 0;
				moving = false;
				moving = testNextMove();
			    }
		    }			       
		break;
	    case 'D':
		yi++;
		if(yi>(yl-1))
		    {
			yi = 0;
			sy++;

		    }

		if(!goDown())
		    {
			moving = false;
			moving = testNextMove();
		    }

		break;
	    case 'L':
		xi--;
		if(xi<0)
		    {

			if(goLeft())
			    {
				sx--;
				if(sx<0)
				    sx = 8;
				xi = xl-1;
			    }
			else
			    {
				xi = 0;
				moving = false;
				moving = testNextMove();
			    }
		    }
		break;
	    case 'R':
		xi++;
		if(xi>(xl-1))
		    {
			xi = 0;
			sx++;
			if(sx>8)
			    sx = 0;
		    }

		if(!goRight())
		    {
			moving = false;
			moving = testNextMove();
		    }

		break;
	    }/*end switch*/
    }


    public void keypress(char c)
    {

	if(!moving)
	    {
		nextdir = ' ';
		switch(c)
		    {
		    case 'U':
			if(goUp())
			    {
				dir = 'U';
				moving = true;
			    }
			break;
		    case 'D':
			if(goDown())
			    {
				dir = 'D';
				moving = true;
			    }
			break;
		    case 'L':
			if(goLeft())
			    {
				dir = 'L';
				moving = true;
			    }
			break;
		    case 'R':
			if(goRight())
			    {
				dir = 'R';
				moving = true;
			    }
			break;
		    }/*end switch*/
		return;
	    }

	if(xi == 0 && yi == 0)
	    {

		switch (c)
		    {
		    case 'U':
			if(!goUp())
			    {
				nextdir = c;
				break;
			    }
			dir = c;
			nextdir = ' ';
			break;
		    case 'D':
			if(!goDown())
			    {
				nextdir = c;
				break;
			    }
			dir = c;
			nextdir = ' ';
			break;
		    case 'L':
			if(!goLeft())
			    {
				nextdir = c;
				break;
			    }
			dir = c;
			nextdir = ' ';
			break;
		    case 'R':
			if(!goRight())
			    {
				nextdir = c;
				break;
			    }
			dir = c;
			nextdir = ' ';
			break;
		    }/*end switch*/

	    }
	else /*if not*/
	    {

		switch(dir)
		    {
		    case 'U':
			if(c == 'D')
			    {
				dir = c;
				nextdir = ' ';
			    }
			else
			    {
				nextdir = c;
			    }
			break;
		    case 'D':
			if(c == 'U')
			    {
				dir = c;
				nextdir = ' ';
			    }
			else
			    {
				nextdir = c;
			    }
			break;
		    case 'L':
			if(c == 'R')
			    {
				dir = c;
				nextdir = ' ';
			    }
			else
			    {
				nextdir = c;
			    }
			break;
		    case 'R':
			if(c == 'L')
			    {
				dir = c;
				nextdir = ' ';
			    }
			else
			    {
				nextdir = c;
			    }
			break;
		    }/*end swtich*/
	     
	    }/*end else we are in the middle of a move.*/

    }

    public void animate()
    {

	int a,b,c,d,e,f,g,h,i,j,m;

	if(!moving)
	    return ;

	m = p[sy][sx];

	a = 0;
	b = 0;
	c = 0;
	d = 0;
	e = 0;
	f = 0;
	g = 0;
	h = 0;
	i = 0;
	j = 0;

	if(debtorman.bit1(m))
	    a = 1;

	if(debtorman.bit2(m))
	    b = 2;

	if(debtorman.bit4(m))
	    c = 4;

	if(debtorman.bit8(m))
	    d = 8;

	if(debtorman.bit16(m))
	    e = 16;

	if(debtorman.bit32(m))
	    f = 32;

	if(debtorman.bit64(m))
	    g = 64;

	if(debtorman.bit128(m))
	    h = 128;

	if(debtorman.bit256(m))
	    i = 256;

	if(debtorman.bit512(m))
	    j = 512;

	int oldscore = score;

	if(yi==0)
	    {
		switch( 5*xi/xl)
		    {
		    case 0:
			m = m - e;
			if(e != 0)
			    {
				score += 3;
			    }
			m = m - j;
			if(j!=0)
			    {
				score += 3;
				atePowerPellet = true;
			    }
			break;
		    case 1:
			if(d!=0)
			    {
				score += 3;
			    }
			m = m - d;
			break;
		    case 2:
			if(c!=0)
			    {
				score += 3;
			    }
			m = m - c;
			break;
		    case 3:
			if(b!=0)
			    {
				score += 3;
			    }
			m = m - b;
			break;
		    case 4:
			if(a!=0)
			    {
				score += 3;
			    }
			m = m - a;
			break;
		    }/*end switch*/
	    }/*yi == 0*/
	else
	    {
		switch( 5*yi/yl)
		    {
		    case 0:
			m = m - e;
			if(e!=0)
			    {
				score += 3;
			    }
			m = m - j;
			if(j!=0)
			    {
				score += 3;
				atePowerPellet = true;
			    }
			break;
		    case 1:
			if(f!=0)
			    {
				score += 3;
			    }
			m = m - f;
			break;
		    case 2:
			if(g!=0)
			    {
				score += 3;
			    }
			m = m - g;
			break;
		    case 3:
			if(h!=0)
			    {
				score += 3;
			    }
			m = m - h;
			break;
		    case 4:
			if(i!=0)
			    {
				score += 3;
			    }
			m = m - i;
			break;
		    }/*end switch*/
	    }/*else yi!=0*/


	p[sy][sx] = m;

	plinkcountdown --;

	if(plinkcountdown < 1)
	    plinkcountdown = 0;

	if( plinkcountdown == 0 )
	    {
		if(oldscore != score )
		    {
			plinkcountdown = 6;
			//debtorman.soundManager.play(debtorman.plink);
			SoundEffect.PLINK.play();
		    }
		/*
		else if(oldscore == score)
		    {
			//debtorman.soundManager.play(debtorman.ticktick);
			SoundEffect.TICK_TICK.play();
		    }
		    */
	    }

	anc ++;
	if(anc>1)
	    anc = 0;
	switch(dir)
	    {
	    case 'U':
		disp = up[anc];
		break;
	    case 'D':
		disp = down[anc];
		break;
	    case 'L':
		disp = left[anc];
		break;
	    case 'R':
		disp = right[anc];
		break;
	    }/*end switch*/
    }

    public boolean touchGhost(ghost gh)
    {

	int x;
	int y;

	int ghx;
	int ghy;

	if(!gh.alive)
	    return false;

	x = sx*50 + 50*xi/xl;
	y = sy*50 + 50*yi/yl;

	ghx = gh.sx*50 + 50*gh.xi/gh.xl;
	ghy = gh.sy*50 + 50*gh.yi/gh.yl;


	if(x<(ghx-10))
	    return false;

	if(x>(ghx+10))
	   return false;

	if(y<(ghy-10))
	    return false;

	if(y>(ghy+10))
	   return false;

	if(gh.beblu>0)
	    {
      		//System.out.println("You Got Him!");
		gh.alive = false;
		gh.plan = false;
	    }
	/*	else
	    {
		System.out.println("He Kicked Your Ass!");
		}*/

	return true;
    }

}/* end of class debtman*/

/************************************************************/
/*           G H O S T   C L A S S   (S C I S S O R S)      */
/************************************************************/

class ghost
{

    public int xi,xl;
    public int yi,yl;
    public int sx,sy;

    public int anc;

    int rank;

    boolean inbox;
    boolean exitb;
    boolean nterb;

    char lastdir = ' ';

    Image [] pics;
    Image [] blu;
    Image [] ded;

    Image disp;

    public boolean alive;

    int beblu;

    int bi;

    char dir;

    int [][]m;

    char tos; /*type of search*/
    int searchd; /*search distance/depth*/
    int smoves; /*numbers of moves on current search*/

    int slimit;
    int rlimit;

    int rmoves;/*number of random moves that must happen*/ 

    boolean plan;

    char [][]mp;
    int [][]gr;

    public ghost(Image [] img,Image [] inblu,Image wereDead[],int [][] mm,int r)
    {
	m = mm;
	pics = img;
	disp = pics[0];

	rank = r;

	ded = wereDead;
	blu = inblu;

	plan = false;

	slimit = 7+(rank);
	rlimit = 5+(rank-1);

	searchd = 10;
	tos = 'S';
	smoves = 0;
	rmoves = rlimit;

	mp = new char[17][9];
	gr = new int [17][9];

	for(int i=0;i<17;i++)
	    for(int j=0;j<9;j++)
		{
		    mp[i][j] = ' ';
		    gr[i][j] = 0;
		}

	xi = rank; /*to give the ghosts a little offset
		     so they cannot "overlay" eachother.*/
	yi = 0;
	xl = 14;
	yl = 14;

	bi = 100*r;

	sx = 4;
	sy = 8;
	dir = 'R';

	anc = 0;

	beblu = 0;

	inbox = true;
	exitb = false;
	nterb = false;

	alive = true;
    }

    public void setMaze(int maze[][])
    {
	m = maze;
    }

    public void setNewLimitByRank(int s,int r,int l)
    {
	setNewLimitByRank(s,r,l,s);
    }

    public void setNewLimitByRank(int s,int r,int l,int sd)
    {

	slimit = s;
	rlimit = r+(rank-1);

	searchd = sd;

	if(slimit<3)
	    {
		slimit = 3;
	    }
	
	if(rlimit<0)
	    {
		rlimit = 0;
	    }

	xl = l;
	yl = l;

    }

    public void totalReset()
    {

	xl = 14;
	yl = 14;

	plan = false;

	slimit = 7+(rank);
	rlimit = 5+(rank-1);

	searchd = 19;
	tos = 'S';

	reset();
    }

    public void reset()
    {
	xi = rank;
	yi = 0;
	beblu = 0;
	inbox = true;
	exitb = false;
	nterb = false;
	sx = 4;
	sy = 8;

	lastdir = ' ';

	bi = 100*rank;

	dir = 'R';
	alive = true;

	smoves = 0;
	rmoves = rlimit;

    }


    public void setBlue(int b)
    {
	beblu = b;
    }

    void moveInBox()
    {

	bi --;
	if(bi < 0)
	    bi = 0; /*time to bust out.*/

	if(sx == 4)
	    {

		if(bi<1 && xi == (xl/2))
		    {
			inbox = false;
			exitb = true;
			yi = yl-1;
			sy --;
			return;
		    }

		if(dir == 'R')
		    {
			xi ++;
			if(xi>=xl)
			    {
				sx ++;
				xi = 0;
				dir = 'L';
			    }

		    }
		else
		    {
			xi --;
			if(xi<0)
			    {
				dir = 'R';
				xi = 0;
			    }
		    }
	    }
	else
	    {
		dir = 'L';
		xi = xl-1;
		sx = 4;
	    }
    }

    public void exitbox()
    {
	int l=0;

	yi--;
	if(yi<1)
	    {
		exitb = false;

		l = (int)(2.0*Math.random());
		switch(l)
		    {
		    case 0:
			dir = 'R';
			break;
		    case 1:
			dir = 'L';
			break;
		    }/*switch*/
	    }
    }

    boolean goLeft()
    {

	if(dir == 'R')
	    return false;

	switch(m[sy][sx])
	    {
	    case 11:
	    case 3:
	    case 4:
	    case 5:
	    case 7:
	    case 9:
	    case 10:
		return true;
	    default:
		return false;
	    }
    }

    boolean goRight()
    {

	if(dir == 'L')
	    return false;

	switch(m[sy][sx])
	    {
	    case 11:
	    case 1:
	    case 2:
	    case 5:
	    case 7:
	    case 8:
	    case 9:
		return true;
	    default:
		return false;
	    }
    }

    boolean goUp()
    {

	if(dir == 'D')
	    return false;

	switch(m[sy][sx])
	    {
	    case 11:
	    case 2:
	    case 3:
	    case 6:
	    case 7:
	    case 8:
	    case 10:
		return true;
	    default:
		return false;
	    }
    }

    boolean goDown()
    {
	if(dir == 'U')
	    return false;

	switch(m[sy][sx])
	    {
	    case 11:
	    case 1:
	    case 4:
	    case 6:
	    case 8:
	    case 9:
	    case 10:
		return true;
	    default:
		return false;
	    }
    }



    boolean goLeft(int x,int y)
    {

	switch(m[y][x])
	    {
	    case 11:
	    case 3:
	    case 4:
	    case 5:
	    case 7:
	    case 9:
	    case 10:
		return true;
	    default:
		return false;
	    }
    }

    boolean goRight(int x,int y)
    {

	switch(m[y][x])
	    {
	    case 11:
	    case 1:
	    case 2:
	    case 5:
	    case 7:
	    case 8:
	    case 9:
		return true;
	    default:
		return false;
	    }
    }

    boolean goUp(int x,int y)
    {

	switch(m[y][x])
	    {
	    case 11:
	    case 2:
	    case 3:
	    case 6:
	    case 7:
	    case 8:
	    case 10:
		return true;
	    default:
		return false;
	    }
    }

    boolean goDown(int x, int y)
    {

	switch(m[y][x])
	    {
	    case 11:
	    case 1:
	    case 4:
	    case 6:
	    case 8:
	    case 9:
	    case 10:
		return true;
	    default:
		return false;
	    }
    }


    void plan(debtman p)
    {
	int xd,yd,ret;

	int i,j;


	if(xi != 0)
	    return;

	if(yi != 0)
	    return;

	/*System.out.println("Plan Smoves : "+smoves+" , rmoves : "+rmoves);*/

	if(rmoves>0)
	    return;

	if (smoves>0)
	    return;

	for(i=0;i<17;i++)
	    for(j=0;j<9;j++)
		{
		    mp[i][j] = ' ';
		    gr[i][j] = 0;
		}

	/*cool we might be able to plan*/
	switch(tos)
	    {
	    case 'D':
		xd = sx-p.sx;
		yd = sy-p.sy;
		if(xd<0)
		    xd*=-1;
		if(yd<0)
		    yd*=-1;
		
		if((xd<=searchd) && (yd<=searchd))
		    {
			
			ret = findway(p.sx,p.sy,sx,sy,999);
			
			if(ret>0)
			    {
				smoves = slimit;
			    }
			else
			    {
				rmoves = rlimit;
			    }
		    }
		
		break;
	    default:
		/*SEARCH STUFF*/
		/*OLDSTUFF*/

		ret = findway(p.sx,p.sy,sx,sy,searchd);

		if(ret>0)
		    {
			//System.out.println("Move Planned!");
			smoves = slimit;
		    }
		else
		    {
			rmoves = rlimit;
			dir = lastdir;
		    }
		break;
	    }
    }


    void setMove()
    {

	if(beblu>0)
	    {
		smoves = 0;
		rmoves = rlimit;
		randomMove();
		return;
	    }

	if((xi == 0) && (yi == 0))
	    {
		char olddir;

		olddir = dir;

		dir = mp[sy][sx];
		if(dir != 'U')
		    if(dir!='D')
			if(dir!='R')
			    if(dir!='L')
				{
				    //System.out.println("Got lost in setMove");
				    //System.out.println("  smoves : "+smoves);
				    dir = olddir;
				    rmoves = rlimit;
				    smoves = 0;
				    randomMove();
				    return;
				}
		smoves--;
		if(smoves<0)
		    smoves = 0;
	    }

	switch(dir)
	    {
	    case 'U':
		yi--;
		break;
	    case 'D':
		yi++;
		break;
	    case 'L':
		xi--;
		break;
	    case 'R':
		xi++;
		break;
	    default:
		/*figure out where you are and get going*/
		//System.out.println("Stuck in random move");
		break;
	    }

	if(xi>=xl)
	    {
		sx++;
		if(sx>8)
		    sx = 0;
		xi = 0;
	    }

	if(yi>=yl)
	    {
		sy++;
		yi = 0;
	    }

	if(xi<0)
	    {
		xi = xl-1;
		sx --;
		if(sx<0)
		    sx = 8;
	    }

	if(yi<0)
	    {
		yi = yl-1;
		sy--;
	    }

    }



    void randomMove()
    {

	if((xi == 0) && (yi == 0))
	    {
		boolean guessagian = true;

		if(beblu<1)
		    {
			rmoves--;
			if(rmoves<0)
			    rmoves = 0;
		    }

		if(!alive && !plan)
		    {
			planWayHome();
			plan = true;
			return;
		    }

		while(guessagian)
		    {
			int mv = 0;
			mv = (int)(4.0*Math.random());

			switch(mv)
			    {
			    case 0:
				if(goUp())
				    {
					dir = 'U';
					guessagian = false;
				    }
				break;
			    case 1:
				if(goDown())
				    {
					dir = 'D';
					guessagian = false;
				    }
				break;
			    case 2:
				if(goRight())
				    {
					dir = 'R';
					guessagian = false;
				    }
				break;
			    case 3:
				if(goLeft())
				    {
					dir = 'L';
					guessagian = false;
				    }
				break;
			    }
		    }

	    }

	switch(dir)
	    {
	    case 'U':
		yi--;
		break;
	    case 'D':
		yi++;
		break;
	    case 'L':
		xi--;
		break;
	    case 'R':
		xi++;
		break;
	    default:
		/*figure out where you are and get going*/
		//System.out.println("Stuck in random move");
		break;
	    }

	if(xi>=xl)
	    {
		sx++;
		if(sx>8)
		    sx = 0;
		xi = 0;
	    }

	if(yi>=yl)
	    {
		sy++;
		yi = 0;
	    }

	if(xi<0)
	    {
		xi = xl-1;
		sx --;
		if(sx<0)
		    sx = 8;
	    }

	if(yi<0)
	    {
		yi = yl-1;
		sy--;
	    }

    }

    public void enterBox()
    {
	yi++;
	if(yi>=yl)
	    {
		sy++;
		yi = 0;
		alive = true;
		nterb = false;
		inbox = true;
		plan = false;
		bi = 100;
	    }
    }

    public void deadMove()
    {
	int oldxi,oldyi;
	oldxi = xi;
	oldyi = yi;

	int osx,osy;
	osx = sx;
	osy = sy;

	char last_dir = ' ';

	if((sx == 4) && (sy == 7))
	    {
		if(xi >= (xl / 2 -1) && xi <= (xl / 2 +1))
		    {
			nterb = true;
			dir = 'L';
			return ;
		    }
	    }

	if((xi == 0) && (yi == 0))
	    {
		last_dir = dir;
		dir = mp[sy][sx];
	    }

	switch (dir)
	    {
	    case 'U':
		yi--;
		if(yi<0)
		    {
			sy--;
			yi = (yl-1);
		    }
		break;
	    case 'D':
		yi++;
		if(yi>=yl)
		    {
			sy++;
			yi = 0;
		    }
		break;
	    case 'L':
		xi--;
		if(xi<0)
		    {
			sx--;
			xi = (xl-1);
			if(sx<0)
			    sx = 8;
		    }
		break;
	    case 'R':
		xi++;
		if(xi>=xl)
		    {
			sx++;
			if(sx>8)
			    sx = 0;
			xi = 0;
		    }
		break;
	    default:
		/*
		System.out.println("Some how we landed in an unhandled case!");
		System .out.println("sx : " + sx +" sy : " + sy + " last dir "+last_dir);
		System.out.println("osx : "+osx+" osy : "+osy+" Current dir '"+ dir +"'");

		int i,j;
		for(i=0;i<17;i++)
		    {
			for(j=0;j<9;j++)
			    {
				System.out.print(mp[i][j]);
			    }
			System.out.println();
		    }
		*/
		break;
	    }

	if(oldxi == xi && oldyi == yi)
	    {
		//System.out.println("deadMove(): We are stuck!!!!");
		xi = 0;
		yi = 0;
		planWayHome();
	    }


    }

    public void move()
    {

	if(xi !=0 && yi !=0)
	    lastdir = dir; /*just so we remember*/

	if(!alive && !nterb && plan)
	    {
		deadMove();
		return;
	    }

	if(beblu>0)
	    {
		beblu--;
		if((beblu%2)==0)
		    return;
	    }

	if(inbox)
	    {
		moveInBox();
		return;
	    }

	if(exitb)
	    {
		exitbox();
		return;
	    }

	if(nterb)
	    {
		enterBox();
		return;
	    }

	if(smoves>0)
	    {
		setMove();
		return;
	    }

	randomMove();
    }

    public void animate()
    {

	int flicker = (beblu%6);

	if (!alive)
	    {
		beblu = 0;
		disp = ded[0];
		return ;
	    }

	if(beblu>0)
	    {
		if(beblu>64)
		    disp = blu[0];
		else
		    {
			if(flicker>2)
			    disp = blu[0];
			else
			    disp = blu[1];
		    }
		return;
	    }

	anc ++;
	if(anc>2)
	    anc = 0;
	disp = pics[anc];
    }

    int findway(int tx,int ty,int x,int y,int depth)
    {
	int dx,dy;
	int ret;
	boolean g_up = false;
	boolean g_right = false;

	int [] dd;
	int i;

	dd = new int[4];

	for(i=0;i<4;i++)
	    dd[i]=i;


	if(depth<0)
	    return 0;

	dx = tx-x;
	dy = ty-y;

	if(dx>0)
	    g_right = true;

	if(dy<0)
	    g_up = true;

	if(dx<0)
	    dx *= -1;

	if(dy<0)
	    dy *= -1;

	if(dx>dy)
	    {
		if(g_right)
		    {
			if(g_up)
			    {
				dd[2]=0;
				dd[0]=1;
				dd[3]=2;
				dd[1]=3;
			    }
			else
			    {
				dd[2]=0;
				dd[1]=1;
				dd[3]=2;
				dd[0]=3;
			    }
		    }
		else
		    {
			if(g_up)
			    {
				dd[3]=0;
				dd[0]=1;
				dd[2]=2;
				dd[1]=3;
			    }
			else
			    {
				dd[3]=0;
				dd[1]=1;
				dd[2]=2;
				dd[0]=3;
			    }
		    }
	    }
	else
	    {
		if(g_up)
		    {
			if(g_right)
			    {
				dd[0]=0;
				dd[2]=1;
				dd[1]=2;
				dd[3]=3;
			    }
			else
			    {
				dd[0]=0;
				dd[3]=1;
				dd[1]=2;
				dd[2]=3;
			    }
		    }
		else
		    {
			if(g_right)
			    {
				dd[1]=0;
				dd[2]=1;
				dd[0]=2;
				dd[3]=3;
			    }
			else
			    {
				dd[1]=0;
				dd[3]=1;
				dd[0]=2;
				dd[2]=3;
			    }
		    }
	    }

	ret = 0;

	if(x<0)
	    x = 8;

	if(x>8)
	    x = 0;

	if(y<0)
	    return 0;

	if(y>16)
	    return 0;

	if(gr[y][x]>0)
	    return 0;

	if(x==tx && y==ty)
	    {
		return 1;
	    }

	gr[y][x] = 1;

	for(i=0;i<4;i++)
	    {
		if(ret==0&&goUp(x,y)&&dd[0]==i)
		    {
			ret = findway(tx,ty,x,y-1,depth-1);
			if(ret>0)
			    {
				mp[y][x] = 'U';
				break;
			    }
		    }
		
		if(ret==0&&goDown(x,y)&&dd[1]==i)
		    {
			ret = findway(tx,ty,x,y+1,depth-1);
			if(ret>0)
			    {
				mp[y][x] = 'D';
				break;
			    }
		    }
		
		if(ret==0&&goLeft(x,y)&&dd[3]==i)
		    {
			ret = findway(tx,ty,x-1,y,depth-1);
			if(ret>0)
			    {
				mp[y][x] = 'L';
				break;
			    }
		    }
		
		if(ret==0&&goRight(x,y)&&dd[2]==i)
		    {
			ret = findway(tx,ty,x+1,y,depth-1);
			if(ret>0)
			    {
				mp[y][x] = 'R';
				break;
			    }
		    }
	    }

	gr[y][x] = 0;

        return ret;
    }

    public void planWayHome()
    {
	int i,j;

	int x,y;

	x = 4;
	y = 7;

	for(i=0;i<17;i++)
	    for(j=0;j<9;j++)
		{
		    mp[i][j] = ' ';
		    gr[i][j] = 0;
		}

	int ret = findway(x,y,sx,sy,999);

	/*
	if(ret<1)
	    {
		System.out.println("I cannot find my way home.");
	    }
	*/

	mp[y][x] = 'R';

	dir = mp[sy][sx];

	//System.out.println("sx : "+sx+" sy : "+sy+" dir : "+dir);

	xi = 0;
	yi = 0;

	plan = true;
    }

}/*end ghost class*/

/************************************************************/
/*               D E B T O R M A N   A P P L E T            */
/************************************************************/


public class debtorman extends Applet 
	implements Runnable,
	MouseListener,
	KeyListener
{

    int mazea[][]={
	{0,1 ,5 ,9 ,5 ,5 ,9 ,5 ,4 },
	{0,6 ,1 ,7 ,9 ,9 ,7 ,4 ,6 },
	{0,2 ,10,0 ,6 ,6 ,0 ,8 ,3 },
	{0,0 ,8 ,9 ,10,8 ,9 ,10,0 },
	{5,9 ,3 ,6 ,2 ,3 ,6 ,2 ,9 },
	{0,8 ,5 ,7 ,4 ,1 ,7 ,5 ,10},
	{0,8 ,4 ,1 ,3 ,2 ,4 ,1 ,10},
	{0,6 ,6 ,8 ,5 ,5 ,10,6 ,6 },
	{0,6 ,8 ,10,0 ,0 ,8 ,10,6 },
	{0,8 ,3 ,8 ,9 ,9 ,10,2 ,10},
	{0,2 ,9 ,3 ,6 ,6 ,2 ,9 ,3 },
	{0,1 ,7 ,4 ,6 ,6 ,1 ,7 ,4 },
	{5,10,0 ,8 ,10,8 ,10,0 ,8 },
	{0,2 ,4 ,8 ,7 ,7 ,10,1 ,3 },
	{0,0 ,8 ,10,1 ,4 ,8 ,10,0 },
	{0,1 ,3 ,6 ,6 ,6 ,6 ,2 ,4 },
	{0,2 ,5 ,7 ,3 ,2 ,7 ,5 ,3 }
    };

    int pelleta[][]={
	{0,255,31 ,511,31 ,31 ,511,31 ,240},
	{0,960,511,31 ,511,511,31 ,496,960},
	{0,31 ,496,0  ,496,496,0  ,511,16 },
	{0,0  ,511,511,496,511,511,496,0  },
	{0,511,16 ,496,31 ,16 ,496,31 ,496},
	{0,511,31 ,31 ,496,511,31 ,31 ,496},
	{0,511,496,511,16 ,31 ,496,511,496},
	{0,496,496,511,31 ,31 ,496,496,496},
	{0,496,511,496,0  ,0  ,511,496,496},
	{0,511,16 ,511,511,511,496,31 ,496},
	{0,31 ,511,16 ,496,496,31 ,511,16 },
	{0,511,31 ,496,496,496,511,31 ,496},
	{0,496,0  ,511,496,511,496,0  ,496},
	{0,31 ,496,511,31 ,31 ,496,511,16 },
	{0,0  ,511,496,511,496,511,496,0  },
	{0,967,16 ,496,496,496,496,30 ,960},
	{0,31 ,31 ,31 ,16 ,31 ,31 ,31 ,16 }
    };

    int pelletd[][]={
	{0,511,31 ,511,496,511,31 ,31 ,496},
	{0,496,967,31 ,496,496,967,31 ,496},
	{0,511,31 ,496,496,511,511,496,496},
	{0,496,511,31 ,496,496,511,31 ,16 },
	{0,511,496,511,496,31 ,31 ,31 ,16 },
	{0,496,511,16 ,496,511,31 ,496,0  },
	{0,511,511,31 ,16 ,511,31 ,511,496},
	{0,496,496,511,31 ,31 ,496,496,496},
	{0,31 ,16 ,496,0  ,0  ,511,511,16 },
	{0,511,496,31 ,31 ,31 ,16 ,511,496},
	{0,511,511,511,31 ,511,31 ,511,496},
	{0,31 ,496,496,511,31 ,511,16 ,496},
	{0,0  ,496,511,496,967,31 ,496,496},
	{0,511,31 ,31 ,511,496,511,31 ,496},
	{0,511,31 ,511,511,31 ,496,511,496},
	{0,496,0  ,496,31 ,496,511,16 ,496},
	{0,31 ,31 ,31 ,31 ,31 ,31 ,31 ,16 }

    };

    int pelletc[][]={
	{0,511,31 ,31 ,511,511,31 ,31 ,496},
	{0,496,967,31 ,496,511,30 ,960,496},
	{0,496,496,511,31 ,31 ,496,496,496},
	{0,496,511,31 ,31 ,31 ,31 ,496,496},
	{0,511,31 ,511,31 ,31 ,511,31 ,496},
	{0,511,31 ,31 ,511,511,31 ,31 ,496},
	{0,511,511,511,31 ,31 ,511,511,496},
	{0,496,496,511,31 ,31 ,496,496,496},
	{0,496,511,496,0  ,0  ,511,496,496},
	{0,496,496,511,31 ,31 ,496,496,496},
	{0,511,31 ,31 ,511,511,31 ,31 ,496},
	{0,511,31 ,511,31 ,31 ,511,31 ,496},
	{0,511,511,31 ,31 ,31 ,31 ,511,496},
	{0,496,511,511,31 ,31 ,511,496,496},
	{0,496,240,31 ,511,511,16 ,240,496},
	{0,496,519,31 ,496,511,30 ,512,496},
	{0,31 ,31 ,31 ,31 ,31 ,31 ,31 ,16 }
    };

    int pelletb[][]={
	{0,255,31 ,31 ,31 ,496,511,31 ,240},
	{0,967,511,31 ,511,496,496,510,960},
	{0,496,496,511,496,31 ,31 ,511,16 },
	{0,496,511,16 ,496,511,511,496,0  },
	{0,511,31 ,511,31 ,511,496,31 ,496},
	{0,31 ,496,511,511,496,31 ,511,496},
	{0,0  ,511,16 ,496,31 ,31 ,31 ,496},
	{0,511,16 ,511,31 ,31 ,511,511,16 },
	{0,511,31 ,496,0  ,0  ,496,31 ,496},
	{0,511,496,511,31 ,31 ,496,0  ,496},
	{0,496,511,496,511,496,496,511,16 },
	{0,496,496,511,16 ,511,511,31 ,496},
	{0,496,511,31 ,511,496,31 ,496,496},
	{0,496,511,496,511,31 ,511,496,496},
	{0,31 ,496,496,31 ,511,16 ,511,240},
	{0,967,496,511,31 ,511,31 ,16 ,960},
	{0,31 ,31 ,16 ,0  ,31 ,31 ,31 ,16 }
    };


    int mazeb[][]={
	{0,1 ,5 ,5 ,5 ,4 ,1 ,5 ,4 },
	{0,8 ,9 ,5 ,9 ,10,6 ,1 ,10},
	{0,6 ,6 ,1 ,10,2 ,7 ,11,3 },
	{0,6 ,8 ,3 ,6 ,1 ,9 ,10,0 },
	{0,8 ,7 ,9 ,7 ,11,10,2 ,4 },
	{0,2 ,4 ,8 ,9 ,10,2 ,9 ,10},
	{0,0 ,8 ,3 ,6 ,2 ,5 ,7 ,10},
	{0,1 ,3 ,1 ,7 ,5 ,9 ,9 ,3 },
	{0,8 ,5 ,10,0 ,0 ,6 ,2 ,4 },
	{0,8 ,4 ,8 ,5 ,5 ,10,0 ,6 },
	{5,10,8 ,10,1 ,4 ,6 ,1 ,7 },
	{0,6 ,6 ,8 ,3 ,8 ,11,7 ,4 },
	{0,6 ,8 ,7 ,9 ,10,2 ,4 ,6 },
	{0,6 ,8 ,4 ,8 ,7 ,9 ,10,6 },
	{0,2 ,10,6 ,2 ,9 ,3 ,8 ,10},
	{0,1 ,10,8 ,5 ,11,5 ,3 ,6 },
	{0,2 ,7 ,3 ,0 ,2 ,5 ,5 ,3 }
    };

    int mazec[][]={
	{0,1 ,5 ,5 ,9 ,9 ,5 ,5 ,4 },
	{0,6 ,1 ,5 ,10,8 ,5 ,4 ,6 },
	{0,6 ,6 ,1 ,7 ,7 ,4 ,6 ,6 },
	{5,10,8 ,7 ,5 ,5 ,7 ,10,8 },
	{0,8 ,7 ,9 ,5 ,5 ,9 ,7 ,10},
	{0,8 ,5 ,7 ,9 ,9 ,7 ,5 ,10},
	{0,8 ,9 ,9 ,7 ,7 ,9 ,9 ,10},
	{0,6 ,6 ,8 ,5 ,5 ,10,6 ,6 },
	{0,6 ,8 ,10,0 ,0 ,8 ,10,6 },
	{0,6 ,6 ,8 ,5 ,5 ,10,6 ,6 },
	{0,8 ,7 ,7 ,9 ,9 ,7 ,7 ,10},
	{0,8 ,5 ,9 ,7 ,7 ,9 ,5 ,10},
	{0,8 ,9 ,7 ,5 ,5 ,7 ,9 ,10},
	{5,10,8 ,9 ,5 ,5 ,9 ,10,8 },
	{0,6 ,6 ,2 ,9 ,9 ,3 ,6 ,6 },
	{0,6 ,2 ,5 ,10,8 ,5 ,3 ,6 },
	{0,2 ,5 ,5 ,7 ,7 ,5 ,5 ,3 }
    };

    int mazed[][]={
	{0,1 ,5 ,9 ,4 ,1 ,5 ,5 ,4 },
	{0,6 ,1 ,7 ,10,6 ,1 ,5 ,10},
	{0,8 ,7 ,4 ,6 ,8 ,11,4 ,6 },
	{0,6 ,1 ,7 ,10,6 ,8 ,7 ,3 },
	{5,11,10,1 ,10,2 ,7 ,5 ,5 },
	{0,6 ,8 ,3 ,6 ,1 ,5 ,4 ,0 },
	{0,8 ,11,5 ,3 ,8 ,5 ,11,4 },
	{0,6 ,6 ,1 ,5 ,7 ,4 ,6 ,6 },
	{5,7 ,3 ,6 ,0 ,0 ,8 ,11,7 },
	{0,1 ,4 ,2 ,5 ,5 ,3 ,8 ,4 },
	{0,8 ,11,9 ,5 ,9 ,5 ,11,10},
	{0,2 ,10,6 ,1 ,7 ,9 ,3 ,6 },
	{0,0 ,6 ,8 ,10,1 ,7 ,4 ,6 },
	{5,9 ,7 ,7 ,11,10,1 ,7 ,11},
	{0,8 ,5 ,9 ,11,7 ,10,1 ,10},
	{0,6 ,0 ,6 ,2 ,4 ,8 ,3 ,6 },
	{0,2 ,5 ,7 ,5 ,7 ,7 ,5 ,3 }
    };

    /*SPEED CONTROL*/
    public static int DELAYTIME = 40;

    /*AUDIO*/
    /*
    public static AudioClip snipping = null;
    public static AudioClip dropping = null;
    public static AudioClip gotaghost = null;
    public static AudioClip plink = null;
    public static AudioClip ticktick = null;
    public static AudioClip dddding = null;
    public static AudioClip clipclip = null;
    public static AudioClip woowoo = null;
    */

    private static final AudioFormat PLAYBACK_FORMAT =
        new AudioFormat(8000, 16, 1, true, false);

    public static boolean playSnipping = true;
    //public static Sound snipping = null;
    //public static Sound dropping = null;
    //public static Sound gotaghost = null;
    //public static Sound plink = null;
    //public static Sound ticktick = null;
    //public static Sound dddding = null;
    //public static Sound clipclip = null;
    //public static Sound woowoo = null;

    //public static SoundManager soundManager;


    public static int level = 0;
    public static boolean mazebloaded = false;
    public static boolean mazecloaded = false;
    public static boolean mazedloaded = false;

    public static Image db;

    boolean startgame=false;

    public static Image sdb;

    public static Image sp;

    public static Image READY;
    public static Image GO;
    public static Image CLEAR;

    public static Image SPLASH;
    public static Image STARTBUTTON;

    public static Image GAMEOVER[];

    public static Image [] maze;

    public static Image [] maze1;

    public static Image [] maze2;

    public static Image [] maze3;

    public static Image [] maze4;

    public static Image [] dml;
    public static Image [] dmr;
    public static Image [] dmu;
    public static Image [] dmd;

    public static Image [] die;

    public static Image [] plt;

    public static Image pnk[];

    public static Image cyn[];

    public static Image grn[];

    public static Image red[];

    public static Image pst[];

    public static Image dgh[];

    public static Image pts[];

    public static Image num[];
    public static Image LIVES;
    public static Image SCORE;
    public static Image BLANK;

    Graphics superg = null;

    debtman player;

    ghost [] gh;

    static int slives=0;
    static int sscore=0;

    public static boolean woowooloop = false;

    public void init()
    {

	READY = null;
	GO = null;
	CLEAR = null;

	SPLASH = null;
	STARTBUTTON=null;

	level = 0;

	LIVES = null;
	SCORE = null;
	BLANK = null;

	GAMEOVER = new Image[4];

	num = new Image[10];

	maze = new Image[20];

	maze1 = new Image[20];

	maze2 = new Image[18];

	maze3 = new Image[18];

	maze4 = new Image[18];

	dml = new Image[4];
	dmr = new Image[4];
	dmd = new Image[4];
	dmu = new Image[4];

	pnk = new Image[3];

	cyn = new Image[3];

	grn = new Image[3];

	red = new Image[3];

	pst = new Image[2];

	dgh = new Image[1];

	plt = new Image[2];

	die = new Image[10];

	pts = new Image[4];

	addKeyListener(this);
	addMouseListener(this);

	player = null;
	gh = new ghost[4];
    }

    public void mouseClicked(MouseEvent e)
    {
    }
    public void mousePressed(MouseEvent e)
    {
	System.out.println("Start Game!");
	startgame = true;
    }
    public void mouseReleased(MouseEvent e){}
    public void mouseEntered(MouseEvent e){}
    public void mouseExited(MouseEvent e){}

    static boolean bit512(int m)
    {
	int i;

	i = m % 1024;
	i = i >> 9;

	if(i>0)
	    return true;
	
	return false;
    }

    static boolean bit256(int m)
    {
	int i;

	i = m % 512;
	i = i >> 8;

	if(i>0)
	    return true;
	
	return false;
    }

    static boolean bit128(int m)
    {
	int i;

	i = m % 256;
	i = i >> 7;

	if(i>0)
	    return true;
	
	return false;
    }

    static boolean bit64(int m)
    {
	int i;

	i = m % 128;
	i = i >> 6;

	if(i>0)
	    return true;
	
	return false;
    }

    static boolean bit32(int m)
    {
	int i;

	i = m % 64;
	i = i >> 5;

	if(i>0)
	    return true;
	
	return false;
    }

    static boolean bit16(int m)
    {
	int i;

	i = m % 32;
	i = i >> 4;

	if(i>0)
	    return true;
	
	return false;
    }

    static boolean bit8(int m)
    {
	int i;

	i = m % 16;
	i = i >> 3;

	if(i>0)
	    return true;
	
	return false;
    }

    static boolean bit4(int m)
    {
	int i;

	i = m % 8;
	i = i >> 2;

	if(i>0)
	    return true;
	
	return false;
    }

    static boolean bit2(int m)
    {
	int i;

	i = m % 4;
	i = i >> 1;

	if(i>0)
	    return true;
	
	return false;
    }

    static boolean bit1(int m){
	int i;

	i = m % 2;

	if(i>0)
	    return true;
	
	return false;
    }

    public void setMazeAll(int mazenum)
    {
	int i;
	int ghostlimit;

	System.gc(); /*Good time to sweep up stuff*/

	/*mazenum = 0;*//*BUGBUG*/
	for(i=0;i<20;i++)
	    {
		maze[i]=maze1[i];
	    }
	for(ghostlimit = 0;ghostlimit < 4;ghostlimit++)
	    {
		gh[ghostlimit].setMaze(mazea);
	    }
	player.setMaze(mazea);
	player.setPellet(pelleta);

	switch(mazenum)
	    {
	    case 1:
		if(!mazebloaded)
		    loadMazeB();
		for(i=1;i<19;i++)
		    {
			maze [i] = maze2[(i-1)];
		    }
		for(ghostlimit = 0;ghostlimit < 4;ghostlimit++)
		    {
			gh[ghostlimit].setMaze(mazeb);
		    }
		player.setMaze(mazeb);
		player.setPellet(pelletb);
		break;

	    case 2:
		if(!mazecloaded)
		    loadMazeC();
		for(i=1;i<19;i++)
		    {
			maze [i] = maze3[(i-1)];
		    }
		for(ghostlimit = 0;ghostlimit < 4;ghostlimit++)
		    {
			gh[ghostlimit].setMaze(mazec);
		    }
		player.setMaze(mazec);  
		player.setPellet(pelletc);
		break;

	    case 3:
		if(!mazedloaded)
		    loadMazeD();
		for(i=1;i<19;i++)
		    {
			maze [i] = maze4[(i-1)];
		    }
		for(ghostlimit = 0;ghostlimit < 4;ghostlimit++)
		    {
			gh[ghostlimit].setMaze(mazed);
		    }
		player.setMaze(mazed);  
		player.setPellet(pelletd);
		break;
	    default:
		break;
	    }

    }

    void renderrow(int y,int idx,Graphics g)
    {

	int i,m;
	int x;

	x = 0;
	for(i=1;i<9;i++)
	    {
		m = player.p[idx][i];

		if(bit16(m))
		    g.drawImage(plt[0],x+30-2,y+30-2,null);

		if(bit512(m))
		    g.drawImage(plt[1],x+30-8,y+30-8,null);

		if(bit8(m))
		    g.drawImage(plt[0],x+40-2,y+30-2,null);

		if(bit4(m))
		    g.drawImage(plt[0],x+50-2,y+30-2,null);

		if(bit2(m))
		    g.drawImage(plt[0],x+60-2,y+30-2,null);

		if(bit1(m))
		    g.drawImage(plt[0],x+70-2,y+30-2,null);

		if(bit32(m))
		    g.drawImage(plt[0],x+30-2,y+40-2,null);

		if(bit64(m))
		    g.drawImage(plt[0],x+30-2,y+50-2,null);

		if(bit128(m))
		    g.drawImage(plt[0],x+30-2,y+60-2,null);

		if(bit256(m))
		    g.drawImage(plt[0],x+30-2,y+70-2,null);

		x+=50;
	    }
    }

    public void drawScreen(Graphics g)
    {

	int aa,a,b,c,d,e,f,h,hh;

	int yoff;

	d = player.sy+1;

	c = d - 1;
	b = c -1;
	a = b -1;

	e = d + 1;
	f = e + 1;
	h = f + 1;
	hh = h+1;

        yoff = - (50*player.yi)/player.yl;

	if(a<0)
	    {
		a=0;
	    }
	if(b<0)
	    {
		b=0;
	    }
	if(c<0)
	    {
		c=0;
	    }

	if(e>19)
	    {
		e=19;
	    }
	if(f>19)
	    {
		f=19;
	    }
	if(h>19)
	    {
		h=19;
	    }

	if(hh>19)
	    {
		hh=19;
	    }

	g.drawImage(maze[a],0,0   + yoff,null);
	g.drawImage(maze[b],0,50  + yoff,null);
	g.drawImage(maze[c],0,100 + yoff,null);
	g.drawImage(maze[d],0,150 + yoff,null);
	g.drawImage(maze[e],0,200 + yoff,null);
	g.drawImage(maze[f],0,250 + yoff,null);
	g.drawImage(maze[h],0,300 + yoff,null);
	g.drawImage(maze[hh],0,350 + yoff,null);

	aa = a-2;
	if(aa>-1)
	    {
		renderrow(-50 + yoff,aa,g);
	    }

	if((a-1)>-1)
	    {
		renderrow(0+ yoff,(a-1),g);
	    }

	if((b-1)>-1)
	    {
		renderrow(50+ yoff,(b-1),g);
	    }

	if((c-1)>-1)
	    {
		renderrow(100+ yoff,(c-1),g);
	    }

	renderrow(150 + yoff,(d-1),g);

	if((e-1)<17)
	    {
		renderrow(200+ yoff,(e-1),g);
	    }

	if((f-1)<17)
	    {
		renderrow(250+ yoff,(f-1),g);
	    }

	if((h-1)<17)
	    {
		renderrow(300+ yoff,(h-1),g);
	    }

	player.screenx = 50*(player.sx-1)+10+(50*player.xi)/player.xl;
	player.screeny = 160;

	g.drawImage(player.disp,player.screenx,player.screeny,null);

	for(int ghostlimit=0;ghostlimit<4;ghostlimit++)
	    {
		if(gh[ghostlimit].sy>=(a-2) && gh[ghostlimit].sy<=(h-1))
		    {
			int ymod = 0;
			
			if(gh[ghostlimit].sy == (a-2))
			    ymod = -50;
			
			if(gh[ghostlimit].sy ==  (a-1))
			    ymod = 0;
			
			if(gh[ghostlimit].sy ==  (b-1))
			    ymod = 50;
			
			if(gh[ghostlimit].sy ==  (c-1))
			    ymod = 100;
			
			if(gh[ghostlimit].sy == (d-1))
			    ymod = 150;
			
			if(gh[ghostlimit].sy == (e-1))
			    ymod = 200;
			
			if(gh[ghostlimit].sy == (f-1))
			    ymod = 250;
			
			if(gh[ghostlimit].sy == (h-1))
			    ymod = 300;		
			
			g.drawImage(gh[ghostlimit].disp,
				    50*(gh[ghostlimit].sx-1)+10+(50*gh[ghostlimit].xi)/gh[ghostlimit].xl,
				    ymod+10+(50*gh[ghostlimit].yi)/gh[ghostlimit].yl + yoff,null);
		    }
	    }/*ghost limit loop*/
	
    }


    public void drawDeadScreen(Graphics g)
    {

	int aa,a,b,c,d,e,f,h;

	d = player.sy+1;

	c = d - 1;
	b = c -1;
	a = b -1;

	e = d + 1;
	f = e + 1;
	h = f + 1;

	if(a<0)
	    {
		a=0;
	    }
	if(b<0)
	    {
		b=0;
	    }
	if(c<0)
	    {
		c=0;
	    }

	if(e>19)
	    {
		e=19;
	    }
	if(f>19)
	    {
		f=19;
	    }
	if(h>19)
	    {
		h=19;
	    }

	g.drawImage(maze[a],0,0,null);
	g.drawImage(maze[b],0,50,null);
	g.drawImage(maze[c],0,100,null);
	g.drawImage(maze[d],0,150,null);
	g.drawImage(maze[e],0,200,null);
	g.drawImage(maze[f],0,250,null);
	g.drawImage(maze[h],0,300,null);

	aa = a-2;
	if(aa>-1)
	    {
		renderrow(-50,aa,g);
	    }

	if((a-1)>-1)
	    {
		renderrow(0,(a-1),g);
	    }

	if((b-1)>-1)
	    {
		renderrow(50,(b-1),g);
	    }

	if((c-1)>-1)
	    {
		renderrow(100,(c-1),g);
	    }

	renderrow(150,(d-1),g);

	if((e-1)<17)
	    {
		renderrow(200,(e-1),g);
	    }

	if((f-1)<17)
	    {
		renderrow(250,(f-1),g);
	    }

	if((h-1)<17)
	    {
		renderrow(300,(h-1),g);
	    }

    }


    public void readyGo()
    {
	int i;
	Graphics dbg;
		
	for(i=0;i<3;i++)
	    {
		fastpaint();
		superg = getGraphics();
		dbg = db.getGraphics();
		dbg.drawImage(READY,132,125,null);
		superg.drawImage(db,0,0,null);

		try
		    {
			Thread.sleep(600);
		    }
		catch(Exception e)
		    {
		    }

		fastpaint();
		try
		    {
			Thread.sleep(50);
		    }
		catch(Exception e)
		    {
		    }
		
	    }

	fastpaint();
	superg = getGraphics();
	dbg = db.getGraphics();
	dbg.drawImage(GO,171,125,null);
	superg.drawImage(db,0,0,null);
	
	try
	    {
		Thread.sleep(600);
	    }
	catch(Exception e)
	    {
	    }


    }

    public void levelClear()
    {

	/*This is to stop the blue ghost sound when the level is done.*/

	woowooloop = false;

	Graphics dbg;
	int ghostlimit;
	int i;

	for(i=0;i<5;i++)
	    {
		fastpaint();
		if(i % 2 == 0)
		    {
			superg = getGraphics();
			dbg = db.getGraphics();
			dbg.drawImage(CLEAR,15,125,null);
			superg.drawImage(db,0,0,null);
		    }

		try
		    {
			if(i % 2 == 0)
			    Thread.sleep(1000);
			else
			    Thread.sleep(200);
		    }
		catch(Exception e)
		    {
		    }
		
	    }/*end i loop*/

	try
	    {
		Thread.sleep(1000);
	    }
	catch(Exception e)
	    {
	    }
	
	for(ghostlimit=0;ghostlimit<4;ghostlimit++)
	    {
		gh[ghostlimit].reset();
	    }

	player.resetAfterDying();

	level ++;

	/*This is the maze settings switch*/
	switch(level)
	    {
	    case 1:
	    case 0:
		setMazeAll(0);
		break;
	    case 3:
	    case 2:
		setMazeAll(1);
		break;
	    case 5:
	    case 4:
		setMazeAll(2);
		break;
	    case 7:
	    case 6:
		setMazeAll(3);
		break;
	    default:
		setMazeAll((int)(4.0*Math.random()));
		break;
	    }/*end switch*/

	/*This switch tinkers with ghost parameters.*/
	switch(level)
	    {
	    case 1:
		gh[0].setNewLimitByRank(4,3,13,10);
		gh[1].setNewLimitByRank(4,3,13,11);
		gh[2].setNewLimitByRank(4,4,13,12);
		gh[3].setNewLimitByRank(4,4,13,13);
		break;
	    case 2:
		gh[0].setNewLimitByRank(5,3,12,10);
		gh[1].setNewLimitByRank(5,3,12,11);
		gh[2].setNewLimitByRank(5,4,12,12);
		gh[3].setNewLimitByRank(5,4,12,13);
		break;
		
	    case 3:
		gh[0].setNewLimitByRank(6,3,11,10);
		gh[1].setNewLimitByRank(6,3,11,11);
		gh[2].setNewLimitByRank(6,3,11,12);
		gh[3].setNewLimitByRank(6,3,11,13);
		break;
		
	    case 4:
		gh[0].setNewLimitByRank(7,2,10,10);
		gh[1].setNewLimitByRank(7,2,10,11);
		gh[2].setNewLimitByRank(7,3,10,12);
		gh[3].setNewLimitByRank(7,3,10,13);
		break;

	    case 5:
		gh[0].setNewLimitByRank(8,2,8,10);
		gh[1].setNewLimitByRank(8,2,9,11);
		gh[2].setNewLimitByRank(8,2,9,12);
		gh[3].setNewLimitByRank(8,2,9,13);
		break;


	    case 6:
		gh[0].setNewLimitByRank(9,1,7,10);
		gh[1].setNewLimitByRank(9,1,7,11);
		gh[2].setNewLimitByRank(9,1,8,12);
		gh[3].setNewLimitByRank(9,1,8,13);
		break;

	    case 7:
		gh[0].setNewLimitByRank(7,0,6,10);
		gh[1].setNewLimitByRank(7,0,7,11);
		gh[2].setNewLimitByRank(7,0,7,12);
		gh[3].setNewLimitByRank(7,0,8,13);
		break;

	    case 0:
		break;

	    default:
		gh[0].setNewLimitByRank(7,0,6,19);
		gh[1].setNewLimitByRank(7,-1,6,19);
		gh[2].setNewLimitByRank(7,-2,7,19);
		gh[3].setNewLimitByRank(7,-3,8,19);
		break;

	    }/*end switch*/

	readyGo();
    }

    /*In the near future rendering will not be done with the
      paint method. We'll capture our graphics context and then
      draw to it directly.*/
   
    public void paint(Graphics g)
    {

	if(g!=null)
	    {
		superg = g; /*once we know it is valid then we*/
		            /*can call get graphics on the applet*/
                            /*any time to get a valid graphics context*/
	    }
	else
	    return;

	Graphics dbg = db.getGraphics();

	if(dbg == null)
	    return;

	/*
	drawScreen(dbg);
	*/

	splashpaint(true);
	/*
	g.drawImage(db,0,0,null);
	*/
    }

    public void update(Graphics g)
    {
	paint(g);
    }

    public void drawScorePanel(Graphics g)
    {
	int llives=player.lives;
	int lscore=player.score;

	if(sscore == lscore && llives == slives)
	    return;

	int i,x;

	g.drawImage(maze1[0],0,0,null);

	for(i = 0, x = 95; i < 6; i++)
	    {
		g.drawImage(BLANK,x+10,1,null);
		x += 19;
	    }
	lscore = player.score;
	g.drawImage(num[lscore%10],x+10,1,null);
	lscore = lscore/10;
	for(i = 0,x=190; i<6 && (lscore>0);i++)
	    {
		g.drawImage(num[lscore%10],x+10,1,null);
		lscore = lscore/10;
		x -= 19;
	    }

	llives = player.lives;
	g.drawImage(num[llives%10],361+10,1,null);
	llives = llives/10;
	if(llives>0)
	    g.drawImage(num[llives%10],342+10,1,null);
	else
	g.drawImage(BLANK,342+10,1,null);

	g.drawImage(SCORE,10,1,null);

	g.drawImage(LIVES,247+10,1,null);

	g.setColor(Color.gray);

	g.drawLine(0,0,411,0);

	sscore = player.score;
	slives = player.lives;

	return;
    }



    public void fastpaint()
    {

	superg = getGraphics();
	Graphics dbg = db.getGraphics();
	drawScreen(dbg);

	dbg = sp.getGraphics();

	drawScorePanel(dbg);

	superg.drawImage(db,0,0,null);
	superg.drawImage(sp,0,350,null);
    }

    public void splashpaint(boolean showbutton)
    {

	superg = getGraphics();
	Graphics dbg = sdb.getGraphics();
	
	dbg.drawImage(SPLASH,0,0,null);

	if(showbutton)
	    {
		dbg.drawImage(STARTBUTTON,133,156,null);
	    }

	superg.drawImage(sdb,0,0,null);

    }

    public void loadMazeD()
    {
	try
	    {
		maze4[ 0] = getImage(getCodeBase(),"maze4/mazed01.gif");
		maze4[ 1] = getImage(getCodeBase(),"maze4/mazed02.gif");
		maze4[ 2] = getImage(getCodeBase(),"maze4/mazed03.gif");
		maze4[ 3] = getImage(getCodeBase(),"maze4/mazed04.gif");
		maze4[ 4] = getImage(getCodeBase(),"maze4/mazed05.gif");
		maze4[ 5] = getImage(getCodeBase(),"maze4/mazed06.gif");
		maze4[ 6] = getImage(getCodeBase(),"maze4/mazed07.gif");
		maze4[ 7] = getImage(getCodeBase(),"maze4/mazed08.gif");
		maze4[ 8] = getImage(getCodeBase(),"maze4/mazed09.gif");
		maze4[ 9] = getImage(getCodeBase(),"maze4/mazed10.gif");
		maze4[10] = getImage(getCodeBase(),"maze4/mazed11.gif");
		maze4[11] = getImage(getCodeBase(),"maze4/mazed12.gif");
		maze4[12] = getImage(getCodeBase(),"maze4/mazed13.gif");
		maze4[13] = getImage(getCodeBase(),"maze4/mazed14.gif");
		maze4[14] = getImage(getCodeBase(),"maze4/mazed15.gif");
		maze4[15] = getImage(getCodeBase(),"maze4/mazed16.gif");
		maze4[16] = getImage(getCodeBase(),"maze4/mazed17.gif");
		maze4[17] = getImage(getCodeBase(),"maze4/mazed18.gif");
		mazedloaded = true;
	    }
	catch(Exception e)
	    {
		System.out.println("Exception "+e.getMessage());
		Toolkit t = Toolkit.getDefaultToolkit();

		maze4[ 0] = t.getImage("maze4/mazed01.gif");
		maze4[ 1] = t.getImage("maze4/mazed02.gif");
		maze4[ 2] = t.getImage("maze4/mazed03.gif");
		maze4[ 3] = t.getImage("maze4/mazed04.gif");
		maze4[ 4] = t.getImage("maze4/mazed05.gif");
		maze4[ 5] = t.getImage("maze4/mazed06.gif");
		maze4[ 6] = t.getImage("maze4/mazed07.gif");
		maze4[ 7] = t.getImage("maze4/mazed08.gif");
		maze4[ 8] = t.getImage("maze4/mazed09.gif");
		maze4[ 9] = t.getImage("maze4/mazed10.gif");
		maze4[10] = t.getImage("maze4/mazed11.gif");
		maze4[11] = t.getImage("maze4/mazed12.gif");
		maze4[12] = t.getImage("maze4/mazed13.gif");
		maze4[13] = t.getImage("maze4/mazed14.gif");
		maze4[14] = t.getImage("maze4/mazed15.gif");
		maze4[15] = t.getImage("maze4/mazed16.gif");
		maze4[16] = t.getImage("maze4/mazed17.gif");
		maze4[17] = t.getImage("maze4/mazed18.gif");
		mazedloaded = true;
	    }

	MediaTracker MT;
	MT = new MediaTracker(this);

	int i;

	for(i = 0;i<18;i++)
	    MT.addImage(maze4[i],0);

	try{MT.waitForAll();}
	catch(Exception e){}
    }

    public void loadMazeC()
    {
	try
	    {
		maze3[ 0] = getImage(getCodeBase(),"maze3/mazec01.gif");
		maze3[ 1] = getImage(getCodeBase(),"maze3/mazec02.gif");
		maze3[ 2] = getImage(getCodeBase(),"maze3/mazec03.gif");
		maze3[ 3] = getImage(getCodeBase(),"maze3/mazec04.gif");
		maze3[ 4] = getImage(getCodeBase(),"maze3/mazec05.gif");
		maze3[ 5] = getImage(getCodeBase(),"maze3/mazec06.gif");
		maze3[ 6] = getImage(getCodeBase(),"maze3/mazec07.gif");
		maze3[ 7] = getImage(getCodeBase(),"maze3/mazec08.gif");
		maze3[ 8] = getImage(getCodeBase(),"maze3/mazec09.gif");
		maze3[ 9] = getImage(getCodeBase(),"maze3/mazec10.gif");
		maze3[10] = getImage(getCodeBase(),"maze3/mazec11.gif");
		maze3[11] = getImage(getCodeBase(),"maze3/mazec12.gif");
		maze3[12] = getImage(getCodeBase(),"maze3/mazec13.gif");
		maze3[13] = getImage(getCodeBase(),"maze3/mazec14.gif");
		maze3[14] = getImage(getCodeBase(),"maze3/mazec15.gif");
		maze3[15] = getImage(getCodeBase(),"maze3/mazec16.gif");
		maze3[16] = getImage(getCodeBase(),"maze3/mazec17.gif");
		maze3[17] = getImage(getCodeBase(),"maze3/mazec18.gif");
		mazecloaded = true;
	    }
	catch(Exception e)
	    {
		System.out.println("Exception "+e.getMessage());
		Toolkit t = Toolkit.getDefaultToolkit();

		maze3[ 0] = t.getImage("maze3/mazec01.gif");
		maze3[ 1] = t.getImage("maze3/mazec02.gif");
		maze3[ 2] = t.getImage("maze3/mazec03.gif");
		maze3[ 3] = t.getImage("maze3/mazec04.gif");
		maze3[ 4] = t.getImage("maze3/mazec05.gif");
		maze3[ 5] = t.getImage("maze3/mazec06.gif");
		maze3[ 6] = t.getImage("maze3/mazec07.gif");
		maze3[ 7] = t.getImage("maze3/mazec08.gif");
		maze3[ 8] = t.getImage("maze3/mazec09.gif");
		maze3[ 9] = t.getImage("maze3/mazec10.gif");
		maze3[10] = t.getImage("maze3/mazec11.gif");
		maze3[11] = t.getImage("maze3/mazec12.gif");
		maze3[12] = t.getImage("maze3/mazec13.gif");
		maze3[13] = t.getImage("maze3/mazec14.gif");
		maze3[14] = t.getImage("maze3/mazec15.gif");
		maze3[15] = t.getImage("maze3/mazec16.gif");
		maze3[16] = t.getImage("maze3/mazec17.gif");
		maze3[17] = t.getImage("maze3/mazec18.gif");
		mazecloaded = true;
	    }

	MediaTracker MT;
	MT = new MediaTracker(this);

	int i;

	for(i = 0;i<18;i++)
	    MT.addImage(maze3[i],0);

	try{MT.waitForAll();}
	catch(Exception e){}
    }

    public void loadMazeB()
    {
	try
	    {
		maze2[ 0] = getImage(getCodeBase(),"maze2/mazeb01.gif");
		maze2[ 1] = getImage(getCodeBase(),"maze2/mazeb02.gif");
		maze2[ 2] = getImage(getCodeBase(),"maze2/mazeb03.gif");
		maze2[ 3] = getImage(getCodeBase(),"maze2/mazeb04.gif");
		maze2[ 4] = getImage(getCodeBase(),"maze2/mazeb05.gif");
		maze2[ 5] = getImage(getCodeBase(),"maze2/mazeb06.gif");
		maze2[ 6] = getImage(getCodeBase(),"maze2/mazeb07.gif");
		maze2[ 7] = getImage(getCodeBase(),"maze2/mazeb08.gif");
		maze2[ 8] = getImage(getCodeBase(),"maze2/mazeb09.gif");
		maze2[ 9] = getImage(getCodeBase(),"maze2/mazeb10.gif");
		maze2[10] = getImage(getCodeBase(),"maze2/mazeb11.gif");
		maze2[11] = getImage(getCodeBase(),"maze2/mazeb12.gif");
		maze2[12] = getImage(getCodeBase(),"maze2/mazeb13.gif");
		maze2[13] = getImage(getCodeBase(),"maze2/mazeb14.gif");
		maze2[14] = getImage(getCodeBase(),"maze2/mazeb15.gif");
		maze2[15] = getImage(getCodeBase(),"maze2/mazeb16.gif");
		maze2[16] = getImage(getCodeBase(),"maze2/mazeb17.gif");
		maze2[17] = getImage(getCodeBase(),"maze2/mazeb18.gif");
		mazebloaded = true;
	    }
	catch(Exception e)
	    {
		System.out.println("Exception "+e.getMessage());
		Toolkit t = Toolkit.getDefaultToolkit();

		maze2[ 0] = t.getImage("maze2/mazeb01.gif");
		maze2[ 1] = t.getImage("maze2/mazeb02.gif");
		maze2[ 2] = t.getImage("maze2/mazeb03.gif");
		maze2[ 3] = t.getImage("maze2/mazeb04.gif");
		maze2[ 4] = t.getImage("maze2/mazeb05.gif");
		maze2[ 5] = t.getImage("maze2/mazeb06.gif");
		maze2[ 6] = t.getImage("maze2/mazeb07.gif");
		maze2[ 7] = t.getImage("maze2/mazeb08.gif");
		maze2[ 8] = t.getImage("maze2/mazeb09.gif");
		maze2[ 9] = t.getImage("maze2/mazeb10.gif");
		maze2[10] = t.getImage("maze2/mazeb11.gif");
		maze2[11] = t.getImage("maze2/mazeb12.gif");
		maze2[12] = t.getImage("maze2/mazeb13.gif");
		maze2[13] = t.getImage("maze2/mazeb14.gif");
		maze2[14] = t.getImage("maze2/mazeb15.gif");
		maze2[15] = t.getImage("maze2/mazeb16.gif");
		maze2[16] = t.getImage("maze2/mazeb17.gif");
		maze2[17] = t.getImage("maze2/mazeb18.gif");
		mazebloaded = true;
	    }

	MediaTracker MT;
	MT = new MediaTracker(this);

	int i;

	for(i = 0;i<18;i++)
	    MT.addImage(maze2[i],0);

	try{MT.waitForAll();}
	catch(Exception e){}

    }

    public void start()
    {
	//soundManager = new SoundManager(PLAYBACK_FORMAT, 16);
	try
	    {
		/*AUDIO*/
		/*
		snipping = getAudioClip(getDocumentBase(),"audio/snipping.au");
		dropping = getAudioClip(getDocumentBase(),"audio/dropping.au");
		gotaghost = getAudioClip(getDocumentBase(),"audio/cashring.au");
		plink = getAudioClip(getDocumentBase(),"audio/plink.au");
		ticktick = getAudioClip(getDocumentBase(),"audio/ticktick.au");
		dddding = getAudioClip(getDocumentBase(),"audio/dddding.au");
		clipclip = getAudioClip(getDocumentBase(),"audio/clipclip.au");
		woowoo = getAudioClip(getDocumentBase(),"audio/woowoo.au");
		*/
		//snipping = soundManager.getSound(getDocumentBase(),"snipping.wav");
		//dropping = soundManager.getSound(getDocumentBase(),"dropping.wav");
		//gotaghost = soundManager.getSound(getDocumentBase(),"cashring.wav");
		//plink =  soundManager.getSound(getDocumentBase(),"plink.wav");
		//ticktick = soundManager.getSound(getDocumentBase(),"ticktick.wav");
		//dddding = soundManager.getSound(getDocumentBase(),"dddding.wav");
		//clipclip = soundManager.getSound(getDocumentBase(),"clipclip.wav");
		//woowoo = soundManager.getSound(getDocumentBase(),"woowoo.wav");

	    }
	catch (Exception e)
	    {


		//snipping = soundManager.getSound("snipping.wav");
		//dropping = soundManager.getSound("dropping.wav");
		//gotaghost = soundManager.getSound("cashring.wav");
		//plink =  soundManager.getSound("plink.wav");
		//ticktick = soundManager.getSound("ticktick.wav");
		//dddding = soundManager.getSound("dddding.wav");
		//clipclip = soundManager.getSound("clipclip.wav");
		//woowoo = soundManager.getSound("woowoo.wav");
	    }

	try
	    {
		maze1[ 0] = getImage(getCodeBase(),"images/maze00.gif");
		maze1[ 1] = getImage(getCodeBase(),"images/maze01.gif");
		maze1[ 2] = getImage(getCodeBase(),"images/maze02.gif");
		maze1[ 3] = getImage(getCodeBase(),"images/maze03.gif");
		maze1[ 4] = getImage(getCodeBase(),"images/maze04.gif");
		maze1[ 5] = getImage(getCodeBase(),"images/maze05.gif");
		maze1[ 6] = getImage(getCodeBase(),"images/maze06.gif");
		maze1[ 7] = getImage(getCodeBase(),"images/maze07.gif");
		maze1[ 8] = getImage(getCodeBase(),"images/maze08.gif");
		maze1[ 9] = getImage(getCodeBase(),"images/maze09.gif");
		maze1[10] = getImage(getCodeBase(),"images/maze10.gif");
		maze1[11] = getImage(getCodeBase(),"images/maze11.gif");
		maze1[12] = getImage(getCodeBase(),"images/maze12.gif");
		maze1[13] = getImage(getCodeBase(),"images/maze13.gif");
		maze1[14] = getImage(getCodeBase(),"images/maze14.gif");
		maze1[15] = getImage(getCodeBase(),"images/maze15.gif");
		maze1[16] = getImage(getCodeBase(),"images/maze16.gif");
		maze1[17] = getImage(getCodeBase(),"images/maze17.gif");
		maze1[18] = getImage(getCodeBase(),"images/maze18.gif");

		dml [0] = getImage(getCodeBase(),"images/dml1.gif");
		dml [1] = getImage(getCodeBase(),"images/dml3.gif");

		dmr [0] = getImage(getCodeBase(),"images/dmr1.gif");
		dmr [1] = getImage(getCodeBase(),"images/dmr3.gif");

		dmd [0] = getImage(getCodeBase(),"images/dmd1.gif");
		dmd [1] = getImage(getCodeBase(),"images/dmd3.gif");

		dmu [0] = getImage(getCodeBase(),"images/dmu1.gif");
		dmu [1] = getImage(getCodeBase(),"images/dmu3.gif");

		plt[0] = getImage(getCodeBase(),"images/smallpellet.gif");
		plt[1] = getImage(getCodeBase(),"images/bigpellet.gif");

		pnk[0] = getImage(getCodeBase(),"images/pink1.gif");
		pnk[1] = getImage(getCodeBase(),"images/pink2.gif");
		pnk[2] = getImage(getCodeBase(),"images/pink3.gif");

		cyn[0] = getImage(getCodeBase(),"images/cyan3.gif");
		cyn[1] = getImage(getCodeBase(),"images/cyan2.gif");
		cyn[2] = getImage(getCodeBase(),"images/cyan1.gif");

		grn[0] = getImage(getCodeBase(),"images/green2.gif");
		grn[1] = getImage(getCodeBase(),"images/green3.gif");
		grn[2] = getImage(getCodeBase(),"images/green1.gif");

		red[0] = getImage(getCodeBase(),"images/red1.gif");
		red[1] = getImage(getCodeBase(),"images/red2.gif");
		red[2] = getImage(getCodeBase(),"images/red3.gif");

		pst[0] = getImage(getCodeBase(),"images/presant.gif");
		pst[1] = getImage(getCodeBase(),"images/presant2.gif");

		dgh[0] = getImage(getCodeBase(),"images/receipt.gif");

		die[0] = getImage(getCodeBase(),"images/die01.gif");
		die[1] = getImage(getCodeBase(),"images/die02.gif");
		die[2] = getImage(getCodeBase(),"images/die03.gif");
		die[3] = getImage(getCodeBase(),"images/die04.gif");
		die[4] = getImage(getCodeBase(),"images/die05.gif");
		die[5] = getImage(getCodeBase(),"images/die06.gif");
		die[6] = getImage(getCodeBase(),"images/die07.gif");
		die[7] = getImage(getCodeBase(),"images/die08.gif");
		die[8] = getImage(getCodeBase(),"images/die09.gif");
		die[9] = getImage(getCodeBase(),"images/die10.gif");

		pts[0] = getImage(getCodeBase(),"images/200.gif");
		pts[1] = getImage(getCodeBase(),"images/400.gif");
		pts[2] = getImage(getCodeBase(),"images/800.gif");
		pts[3] = getImage(getCodeBase(),"images/1600.gif");

		num[0] = getImage(getCodeBase(),"numbers/r0.gif");
		num[1] = getImage(getCodeBase(),"numbers/r1.gif");
		num[2] = getImage(getCodeBase(),"numbers/r2.gif");
		num[3] = getImage(getCodeBase(),"numbers/r3.gif");
		num[4] = getImage(getCodeBase(),"numbers/r4.gif");
		num[5] = getImage(getCodeBase(),"numbers/r5.gif");
		num[6] = getImage(getCodeBase(),"numbers/r6.gif");
		num[7] = getImage(getCodeBase(),"numbers/r7.gif");
		num[8] = getImage(getCodeBase(),"numbers/r8.gif");
		num[9] = getImage(getCodeBase(),"numbers/r9.gif");

		LIVES = getImage(getCodeBase(),"numbers/lives.gif");
		SCORE = getImage(getCodeBase(),"numbers/score.gif");
		BLANK = getImage(getCodeBase(),"numbers/rb.gif");

		READY = getImage(getCodeBase(),"images/ready.gif");
		GO = getImage(getCodeBase(),"images/go.gif");
		CLEAR = getImage(getCodeBase(),"images/clear.gif");

		SPLASH =  getImage(getCodeBase(),"images/splash.gif");
		STARTBUTTON =  getImage(getCodeBase(),"images/startbutton.gif");

		GAMEOVER[0] = getImage(getCodeBase(),"images/gameover01.gif");
		GAMEOVER[1] = getImage(getCodeBase(),"images/gameover02.gif");
		GAMEOVER[2] = getImage(getCodeBase(),"images/gameover03.gif");
		GAMEOVER[3] = getImage(getCodeBase(),"images/gameover04.gif");
	    }
	catch(Exception e)
	    {
		System.out.println("Exception "+e.getMessage());
		Toolkit t = Toolkit.getDefaultToolkit();

		maze1[ 0] = t.getImage("images/maze00.gif");
		maze1[ 1] = t.getImage("images/maze01.gif");
		maze1[ 2] = t.getImage("images/maze02.gif");
		maze1[ 3] = t.getImage("images/maze03.gif");
		maze1[ 4] = t.getImage("images/maze04.gif");
		maze1[ 5] = t.getImage("images/maze05.gif");
		maze1[ 6] = t.getImage("images/maze06.gif");
		maze1[ 7] = t.getImage("images/maze07.gif");
		maze1[ 8] = t.getImage("images/maze08.gif");
		maze1[ 9] = t.getImage("images/maze09.gif");
		maze1[10] = t.getImage("images/maze10.gif");
		maze1[11] = t.getImage("images/maze11.gif");
		maze1[12] = t.getImage("images/maze12.gif");
		maze1[13] = t.getImage("images/maze13.gif");
		maze1[14] = t.getImage("images/maze14.gif");
		maze1[15] = t.getImage("images/maze15.gif");
		maze1[16] = t.getImage("images/maze16.gif");
		maze1[17] = t.getImage("images/maze17.gif");
		maze1[18] = t.getImage("images/maze18.gif");

		dml [0] = t.getImage("images/dml1.gif");
		dml [1] = t.getImage("images/dml3.gif");

		dmr [0] = t.getImage("images/dmr1.gif");
		dmr [1] = t.getImage("images/dmr3.gif");

		dmd [0] = t.getImage("images/dmd1.gif");
		dmd [1] = t.getImage("images/dmd3.gif");

		dmu [0] = t.getImage("images/dmu1.gif");
		dmu [1] = t.getImage("images/dmu3.gif");

		plt[0] = t.getImage("images/smallpellet.gif");
		plt[1] = t.getImage("images/bigpellet.gif");

		pnk[0] = t.getImage("images/pink1.gif");
		pnk[1] = t.getImage("images/pink2.gif");
		pnk[2] = t.getImage("images/pink3.gif");

		cyn[0] = t.getImage("images/cyan3.gif");
		cyn[1] = t.getImage("images/cyan2.gif");
		cyn[2] = t.getImage("images/cyan1.gif");

		grn[0] = t.getImage("images/green2.gif");
		grn[1] = t.getImage("images/green3.gif");
		grn[2] = t.getImage("images/green1.gif");

		red[0] = t.getImage("images/red1.gif");
		red[1] = t.getImage("images/red2.gif");
		red[2] = t.getImage("images/red3.gif");

		pst[0] = t.getImage("images/presant.gif");
		pst[1] = t.getImage("images/presant2.gif");

		dgh[0] = t.getImage("images/receipt.gif");

		die[0] = t.getImage("images/die01.gif");
		die[1] = t.getImage("images/die02.gif");
		die[2] = t.getImage("images/die03.gif");
		die[3] = t.getImage("images/die04.gif");
		die[4] = t.getImage("images/die05.gif");
		die[5] = t.getImage("images/die06.gif");
		die[6] = t.getImage("images/die07.gif");
		die[7] = t.getImage("images/die08.gif");
		die[8] = t.getImage("images/die09.gif");
		die[9] = t.getImage("images/die10.gif");

		pts[0] = t.getImage("images/200.gif");
		pts[1] = t.getImage("images/400.gif");
		pts[2] = t.getImage("images/800.gif");
		pts[3] = t.getImage("images/1600.gif");

		num[0] = t.getImage("numbers/r0.gif");
		num[1] = t.getImage("numbers/r1.gif");
		num[2] = t.getImage("numbers/r2.gif");
		num[3] = t.getImage("numbers/r3.gif");
		num[4] = t.getImage("numbers/r4.gif");
		num[5] = t.getImage("numbers/r5.gif");
		num[6] = t.getImage("numbers/r6.gif");
		num[7] = t.getImage("numbers/r7.gif");
		num[8] = t.getImage("numbers/r8.gif");
		num[9] = t.getImage("numbers/r9.gif");

		LIVES = t.getImage("numbers/lives.gif");
		SCORE = t.getImage("numbers/score.gif");
		BLANK = t.getImage("numbers/rb.gif");

		READY = t.getImage("images/ready.gif");
		GO = t.getImage("images/go.gif");
		CLEAR = t.getImage("images/clear.gif");

		SPLASH =  t.getImage("images/splash.gif");
		STARTBUTTON =  t.getImage("images/startbutton.gif");

		GAMEOVER[0] = t.getImage("images/gameover01.gif");
		GAMEOVER[1] = t.getImage("images/gameover02.gif");
		GAMEOVER[2] = t.getImage("images/gameover03.gif");
		GAMEOVER[3] = t.getImage("images/gameover04.gif");
	    }

	MediaTracker MT;
	MT = new MediaTracker(this);

	int i;

	for(i = 0;i<19;i++)
	    MT.addImage(maze1[i],0);

	for(i=0;i<10;i++)
	    {
		MT.addImage(num[i],0);
	    }

	MT.addImage(READY,0);
	MT.addImage(GO,0);
	MT.addImage(CLEAR,0);

	MT.addImage(LIVES,0);
	MT.addImage(SCORE,0);
	MT.addImage(BLANK,0);

	MT.addImage(SPLASH,0);
	MT.addImage(STARTBUTTON,0);

	for(i = 0;i<2;i++)
	    {
		MT.addImage(dml[i],0);
		MT.addImage(dmr[i],0);
		MT.addImage(dmu[i],0);
		MT.addImage(dmd[i],0);

		MT.addImage(plt[i],0);
		MT.addImage(pst[i],0);
	    }

	for(i=0;i<3;i++)
	    {
		MT.addImage(pnk[i],0);
		MT.addImage(cyn[i],0);
		MT.addImage(grn[i],0);
		MT.addImage(red[i],0);
	    }

	for(i=0;i<4;i++)
	    {
		MT.addImage(pts[i],0);
		MT.addImage(GAMEOVER[i],0);
	    }

	for(i=0;i<10;i++)
	    {
		MT.addImage(die[i],0);
	    }

	db = createImage(410,350);

	sp = createImage(410,32);


	sdb = createImage(410,382);

	try{MT.waitForAll();}
	catch(Exception e){}

	System.out.println("Yo Ho!");
	maze1[19] = maze1[0];

	player = new debtman(dml,dmr,dmu,dmd,mazea,pelleta);

	gh[0] = new ghost(pnk,pst,dgh,mazea,4);
	gh[1] = new ghost(cyn,pst,dgh,mazea,3);
	gh[2] = new ghost(grn,pst,dgh,mazea,2);
	gh[3] = new ghost(red,pst,dgh,mazea,1);

	/*setMazeAll(2);*/

	Thread t = new Thread(this);

	setMazeAll(0);/*this is to set the maze array to something non-null*/
	t.start();

    }

    public void stop()
    {
    }

    /*Key Listener Functionality*/

    public void keyTyped(KeyEvent e)
    {
    }

    public void keyPressed(KeyEvent e)
    {
	switch(e.getKeyCode())
	    {
	    case KeyEvent.VK_UP:
		//System.out.println("UP!");
		player.keypress('U');
		break;
	    case KeyEvent.VK_DOWN:
		//System.out.println("DOWN!");
		player.keypress('D');
		break;
	    case KeyEvent.VK_LEFT:
		//System.out.println("LEFT!");
		player.keypress('L');
		break;
	    case KeyEvent.VK_RIGHT:
		//System.out.println("RIGHT!");
		player.keypress('R');
		break;
		
	    default:

		switch(e.getKeyChar())
		    {
		    case '0':
			DELAYTIME=15;
			break;
		    case '9':
			DELAYTIME=20;
			break;
		    case '8':
			DELAYTIME=25;
			break;
		    case '7':
			DELAYTIME=30;
			break;
		    case '6':
			DELAYTIME=35;
			break;
		    case '5':
			DELAYTIME=40;
			break;
		    case '4':
			DELAYTIME=45;
			break;
		    case '3':
			DELAYTIME=50;
			break;
		    case '2':
			DELAYTIME=55;
			break;
		    case '1':
			DELAYTIME=60;
			break;
			
		    case '+':
		    case '=':
			DELAYTIME--;
			break;
		    case '-':
		    case '_':
			DELAYTIME ++;
			break;
		    }/*end switch*/
		
		if(DELAYTIME<10)
		    DELAYTIME=10;

		break;

	    }/*end switch key code.*/
    }

    public void keyReleased(KeyEvent e)
    {

    }

    void ghostPlayerContact(int gn)
    {
	if(player.touchGhost(gh[gn]))
	    {
		if(!gh[gn].alive)
		    {
			//debtorman.soundManager.play(debtorman.gotaghost);
			/*the player has killed the ghost*/
			SoundEffect.CASH_RING.play();
			try
			    {

				for(int y=0;y<20;y++)
				    {
					
					/*You might try something here*/
					superg = getGraphics();
					Graphics dbg = db.getGraphics();
					drawDeadScreen(dbg);

					/*CHANGECHANGE*/
					if(player.points>3)
					    player.points = 3;

					/*this makes the score float up*/
					dbg.drawImage(pts[player.points],
						      player.screenx,
						      player.screeny-y,
						      null);
					superg.drawImage(db,0,0,null);
					try
					    {
						Thread.sleep(50);
					    }
					catch(Exception e)
					    {
					    }
					
				    }/*end y loop*/

				switch(player.points)
				    {
				    case 1:
					player.score += 400;
					break;
				    case 2:
					player.score += 800;
					break;
				    case 3:
					player.score += 1600;
					break;
				    default:
					player.score += 200;
					break;
				    }
				player.points++;
				Thread.sleep(100);
			    }
			catch(Exception e)
			    {
			    }
		    }
		else
		    {
			/*The ghost kills the player*/
			try
			    {
				//debtorman.soundManager.play(debtorman.snipping);
				SoundEffect.SNIPPING.play();
				int k;
				for(k=0;k<30;k++)
				    {
					gh[gn].animate();
					
					try
					    {
						Thread.sleep(30);
					    }
					catch(Exception e)
					    {
					    }
					
					fastpaint();
					
				    }

				//debtorman.soundManager.play(debtorman.dropping);
				SoundEffect.DROPPING.play();
				for(k=0;k<10;k++)
				    {
					
					
					/*You might try something here*/
					superg = getGraphics();
					Graphics dbg = db.getGraphics();
					drawDeadScreen(dbg);
					dbg.drawImage(die[k],player.screenx,player.screeny,null);
					superg.drawImage(db,0,0,null);
					
					try
					    {
						Thread.sleep(500);
					    }
					catch(Exception e)
					    {
					    }
				    }
				try
				    {
					Thread.sleep(1000);
				    }
				catch(Exception e)
				    {
				    }
				/*decrement lives here*/

				player.lives--;

				//System.out.println("You got " + player.lives + " left!");

				player.resetAfterDying();
				for (int ghostlimit=0;ghostlimit<4;ghostlimit++)
				    gh[ghostlimit].reset();
				Thread.sleep(1000);
				
			    }
			catch(Exception e)
			    {
			    }
		    }/*end else the ghost killed the player*/
		
	    }/*player has touched ghost*/
    }

    public void gameOver()
    {
	Graphics superg;
	Graphics dbg;
	int i;
	int sleep = 100;

	for(i=0;i<4;i++)
	    {
		superg = getGraphics();
		dbg = db.getGraphics();

		drawDeadScreen(dbg);

		switch(i)
		    {
		    case 0:
			dbg.drawImage(GAMEOVER[0],178,158,null);
			break;
		    case 1:
			sleep += 50;
			dbg.drawImage(GAMEOVER[1],162,158,null);
			break;
		    case 2:
			sleep += 100;
			dbg.drawImage(GAMEOVER[2],134,158,null);
			break;
		    case 3:
			sleep += 10000;
			dbg.drawImage(GAMEOVER[3],92,158,null);
			break;
		    }/*end switch*/

		dbg = sp.getGraphics();
		drawScorePanel(dbg);

		superg.drawImage(db,0,0,null);
		superg.drawImage(sp,0,350,null);

		try
		    {
			Thread.sleep(sleep);
		    }
		catch(Exception e)
		    {
		    }
	    }/*end i loop*/
    }

    void splashscreen()
    {
	startgame = false;
	int i;
	boolean paintbutton = false;

	while (!startgame)
	    {
		splashpaint(paintbutton);
		paintbutton = !paintbutton;
		for(i=0;i<12 && !startgame;i++)
		    {
			try
			    {
				Thread.sleep(100);
			    }
			catch(Exception e)
			    {
			    }
		    }
	    }
    }
    
    public static void resetSounds()
    {
	playSnipping = true;
    }

    void simpleloop()
    {

	int bonuslife = 10000;
	int bonuslifestep = 20000;

	int snipcountdown = 0;

	slives=0;
	sscore=0;

	/*do big resets here*/

	level = 0;

	player.totalReset();

	for (int ghostlimit=0;ghostlimit<4;ghostlimit++)
	    {
		gh[ghostlimit].totalReset();
	    }

	setMazeAll(0);

	readyGo();

	for(int i = 0;;i++)
	    {
		int ghostlimit;
		if(i>3)
		    i=0;

		resetSounds();

		player.animate();
		if (player.atePowerPellet)
		    {
			//System.out.println("Ate Power Pellet!");
			player.points = 0; /*reset the score*/
			player.atePowerPellet = false;
			for(ghostlimit=0;ghostlimit<4;ghostlimit++)
			    {
				switch(level)
				    {
				    case 0:
				    case 1:
					gh[ghostlimit].setBlue(300);
					break;
				    case 2:
					gh[ghostlimit].setBlue(250);
					break;
				    case 3:
					gh[ghostlimit].setBlue(200);
					break;
				    case 4:
					gh[ghostlimit].setBlue(175);
					break;
				    case 5:
					gh[ghostlimit].setBlue(150);
					break;
				    case 6:
					gh[ghostlimit].setBlue(125);
					break;
				    default:
					gh[ghostlimit].setBlue(100);
				    }
			    }
		    }

		
		if (player.allEaten())
		    {
			levelClear();
			continue;
		    }
		
		boolean onealiveblue=false;
		boolean onealive=false;

		snipcountdown --;
		if(snipcountdown<1)
		    snipcountdown = 0;

		for(ghostlimit=0;ghostlimit<4;ghostlimit++)
		    {
			gh[ghostlimit].animate();

			if(snipcountdown == 0 &&
			   gh[ghostlimit].alive && 
			   gh[ghostlimit].beblu < 1 && 
			   gh[ghostlimit].anc == 0)
			    {
			        snipcountdown = 4;
				//debtorman.soundManager.play(debtorman.clipclip);
			        SoundEffect.CLIP_CLIP.play();
			    }

			if(gh[ghostlimit].alive)
			    {
				onealive = true;
				if(gh[ghostlimit].beblu>0)
				    onealiveblue = true;
			    }

			
		    }
		if(onealiveblue)
		    {
			//debtorman.soundManager.play(debtorman.woowoo);
			SoundEffect.WOO_WOO.playIfNotPlaying();
		    }

		player.move();
		for(ghostlimit=0;ghostlimit<4;ghostlimit++)
		{
		    gh[ghostlimit].move();
		    gh[ghostlimit].plan(player);
		    
		    ghostPlayerContact(ghostlimit);
		}/*ghost limit*/

		if(player.lives<1)
		    {
			gameOver();
			return;
		    }

		if(player.score>=bonuslife)
		    {
			//System.out.println("Ding! Ding! Ding! Ding!");

			player.lives+=1;
			//debtorman.soundManager.play(debtorman.dddding);
			SoundEffect.DING.play();
			bonuslife += bonuslifestep;
			bonuslifestep += 10000;
			if(bonuslifestep > 50000)
			    bonuslifestep = 50000;
		    }

		fastpaint(); /*moved it above delay loop*/

		try
		    {
			/*
			Thread.sleep(40);
			*/
			Thread.sleep(DELAYTIME);
		    }
		catch(Exception e)
		    {
		    }



	    }
    }

    public void run()
    {

    SoundEffect.init();
    	
	while(superg == null)
	    {
		try
		    {
			Thread.sleep(50);
		    }
		catch(Exception e)
		    {
		    }
		
		repaint(0);
	    }
	for(;;)
	    {
		splashscreen();
		simpleloop();/*game play loop*/
	    }
    }

}//end applet definition
