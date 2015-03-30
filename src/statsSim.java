import processing.core.*; 

import java.util.ArrayList; 

public class statsSim extends PApplet {
	
	private static final long serialVersionUID = 1L;
	
class Chip {
  
  private static final int HEIGHT = 200;
  private static final int START_X = 100;
  
  public int col;
  public float proportion;
  
  public Chip(int col, float proportion) {
    this.col = col;
    this.proportion = proportion;
  }
  
  public boolean equals(Chip c) {
    return this.col == c.col;
  }
  
  public void drawChip(int position, boolean selected) {
    fill(this.col);
    for (int i = 0; i < proportion; i++)
      ellipse(START_X + 80 * position, HEIGHT - i * 20, 50, 50);
    if (selected) {
      fill(0xffFFFF00);
      rect(START_X + 80 * position - 25, HEIGHT + 30, 50, 5);
    }
  }
  
  public void increaseProportion() {
    if (proportion < 8)
      proportion++;
  }
  
  public void decreaseProportion() {
    if (proportion > 1f)
      proportion--;
  }
  
}

class Population {
  
  private ArrayList<Chip> chips;
  private float sumProportions;
  
  public Population(ArrayList<Chip> chips) {
    this.chips = chips;
    for (Chip c : chips)
      sumProportions += c.proportion;
  }
  
  public Chip getChip() {
    float chipNum = random(0, sumProportions);
    float counter = 0;
    for (Chip c : chips) {
      counter += c.proportion;
      if (chipNum <= counter)
        return (Chip) c;
    }
    assert 0==1;
    return (Chip) chips.get(chips.size() - 1);
  } 
  
}

class Sample {
  
  private Chip[] individuals;
  
  public Sample(Population p, int size) {
    individuals = new Chip[size];
    for (int i = 0; i < size; i++)
      individuals[i] = p.getChip();
  }
  
  public int countAll() {
    return individuals.length;
  }
  
  public int countType(Chip type) {
    int counter = 0;
    for (Chip c : individuals) {
      if (c.equals(type))
        counter++;
    }
    return counter;
  }
  
  public float getProportion(Chip type) {
    return (float) countType(type) / countAll();
  }
  
  public void drawDistribution(ArrayList<Chip> chips) {
    for (int i = 0; i < chips.size(); i++) {
      fill(chips.get(i).col);
      rect(75, 300 + i * 20, 900 * getProportion(chips.get(i)), 10);
      fill(0);
      text(countType(chips.get(i)), 75 + 900 * getProportion(chips.get(i)) + 20, 10 + 300 + i * 20);
    }
  }
  
  public float getChiSquared(ArrayList<Chip> chips) {
	  float observed, expected, sum = 0, sumProportions = 0;
	  for (Chip c : chips)
		  sumProportions += c.proportion;
	  for (Chip c : chips) {
		  expected = c.proportion / sumProportions;
		  observed = countType(c);
		  sum += (observed - expected) * (observed - expected) / expected;
	  }
	  return sum;
  }
  
}

ArrayList<Chip> chips;
Population p;
Sample sample;
int sampleSize;
int selected;
int lastMouseX;

public void setup() {

  frame.setTitle("Chip Simulation");
  size(900, 540);
  background(200);
  
  chips = new ArrayList<Chip>();
  chips.add(new Chip(0xffFF0000, 2));
  chips.add(new Chip(0xff00FF00, 4));
  chips.add(new Chip(0xff0000FF, 3));
  
  selected = 0;
  sampleSize = 500;
 
  drawSampleSize();
 
}

public void draw() {
  checkMouse();
  for (int i = 0; i < chips.size(); i++)
    chips.get(i).drawChip(i, i==selected);
}

public void drawSampleSize() {
  fill(0);
  text("Sample Size = " + sampleSize, 75, 270);
}

public void drawChiSquaredDistribution() {
	
	final int SAMPLES = 2000;
	final int ORIGIN = 240000;
		
	float[] results = new float[SAMPLES];
	p = new Population(chips);
	for (int i = 0; i < SAMPLES; i++)
		results[i] = (new Sample(p, sampleSize)).getChiSquared(chips);
		
	for (float i = 0; i < 500; i += 1) {
		int inBar = 0;
		for (float result : results) {
			if (i * 50 + ORIGIN <= result && result < (i + 0.5) * 50 + ORIGIN)
				inBar++;
		}
			
		rect(75 + i, 400 - inBar, 0f, inBar);
	}
	
}

public void checkMouse() {
  if (mousePressed) {
    int speed = mouseX - lastMouseX;
    if (sampleSize + speed > 0 && sampleSize + speed < 10000)
      sampleSize += speed;
    background(200);
    drawSampleSize();
  }
  lastMouseX = mouseX;
}

public void keyPressed() {
  background(200);
  drawSampleSize();
  
  if (keyCode == 10) {
    p = new Population(chips);
    sample = new Sample(p, sampleSize);
    sample.drawDistribution(chips);
  }
  
  if (Character.isDigit(key) && key != 48) {
    int value = (int) key - 48;
    if (value > chips.size())
      chips.add(new Chip(color(random(255), random(255), random(255)), (int) random(1,5)));
    else
      selected = value - 1;
  }
  
  if (key == 'd')
	  drawChiSquaredDistribution();
  
  if (keyCode == 37) {
    selected = (selected - 1) % chips.size();
    if (selected < 0)
      selected += chips.size();
  }
  if (keyCode == 39)
    selected = (selected + 1) % chips.size();
  if (keyCode == 38)
    chips.get(selected).increaseProportion();
  if (keyCode == 40)
    chips.get(selected).decreaseProportion();
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "statsSim" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
