import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;

import javax.swing.ImageIcon;

public class Game extends Thread {
	private int delay = 20; //게임의 딜레이
	private long pretime;
	private int cnt; 	//딜레이마다 증가할 cnt (이벤트 발생 주기)
	
	private Image player = new ImageIcon("src/img/en.png").getImage();
	
	private int playerX, playerY;
	private int playerWidth = player.getWidth(null);	//플레이어의 x값
	private int playerHeigth = player.getHeight(null);	//플레이어의 y값
	private int playerSpeed = 10;	//한번에 움직일 거리
	private int playerHp = 30;		//플레이어의 체력
	private int score;
	private boolean up, down, left, right, shooting;	//플레이어의 움직임을 제한
	private boolean isOver;				//끝났는지 안끝났는지 확인함
	
	private ArrayList<PlayerAttack> playerAttackList = new ArrayList<PlayerAttack>();
	private ArrayList<Enemy> enemyList = new ArrayList<Enemy>();
	private ArrayList<EnemyAttack> enemyAttackList = new ArrayList<EnemyAttack>();
	
	private PlayerAttack playerAttack;	//플레이어 어택이라는 클래스를 가져와서 객체변수에 저장
	private Enemy enemy;				//플레이어 어택이라는 클래스를 가져와서 객체변수에 저장
	private EnemyAttack enemyAttack;	//에너미어택이라는 클래스를 가져와서 객체변수에 저장
	
	@Override
	public void run() {		
		reset();
		while(true) {	//delay가 지날때마다 cnt증가
			while(!isOver) {
				pretime = System.currentTimeMillis();	//정확한 주기를 위해
				if(System.currentTimeMillis() - pretime < delay) {	//현재시간 - (cnt가 증가하기 전시간) < delay일 경우 	
					try {
						Thread.sleep(delay - System.currentTimeMillis() + pretime);	//그 차이만큼 스레드에 seelp
						keyProcess();
						playerAttackProcess();
						enemyAppearProcess();
						enemyMoveProcess();
						enemyAttackProcess();
						cnt++;
					} catch (InterruptedException e) {					
							e.printStackTrace();
					}		
				}
			}
			try {
				Thread.sleep(100);
			}catch(InterruptedException e) {
				e.printStackTrace();
			}
		}	
	}
	

	public void reset() {
		cnt = 0;
		playerX = 10;
		playerY = (Main.SCREEEN_HEIGHT - playerHeigth)/2;
		isOver = false;
		score = 0;
		playerHp = 30;
		
		playerAttackList.clear();
		enemyList.clear();
		enemyList.clear();
		
	}



	public void keyProcess() {
		//화면아래로 플레이어가 내려감
		if(up && playerY - playerSpeed > 0) playerY -= playerSpeed;		
		//화면 위로 플레이어가 올라감 단 플레이어의 현재위치 + 이동할 위치 +본인의 크기가 메인화면보다 작을때에만 가능(화면밖을 안넘어가게 해준다)
		if(down && playerY + playerHeigth + playerSpeed < Main.SCREEEN_HEIGHT) playerY += playerSpeed;	
		//화면왼쪽으로 플레이어가 움직인다.
		if(left && playerX - playerSpeed > 0) playerX -= playerSpeed;
		//화면 오른쪽으로 플레이어가 움직인다 단 플레이어의 현재위치 + 이동할 위치 +본인의 크기가 메인화면보다 작을때에만 가능(화면밖을 안넘어가게 해준다)
		if(right && playerX + playerWidth + playerSpeed < Main.SCREEEN_WIDTH) playerX += playerSpeed;	
		if(shooting && cnt % 5	== 0) {
			playerAttack = new PlayerAttack(playerX + 30, playerY+10);
			playerAttackList.add(playerAttack);
		}
	}
	
	private void playerAttackProcess() {
		for(int i = 0; i < playerAttackList.size(); i++) {
			playerAttack = playerAttackList.get(i);
			playerAttack.fire();
			
			for(int j = 0;j < enemyList.size(); j++) {
				enemy = enemyList.get(j);
				if(playerAttack.x > enemy.x && playerAttack.x < enemy.x + enemy.width && playerAttack.y > enemy.y-10 && playerAttack.y < enemy.y+enemy.height+10) {
					enemy.hp -= playerAttack.attack;
					playerAttackList.remove(playerAttack);
				}
				if(enemy.hp <=0 ) {
					enemyList.remove(enemy);
					score +=100;
				}
			}
		}
		
	}
	
	private void enemyAppearProcess() {
		if(cnt % 80 == 0) {
			enemy = new Enemy(1120, (int)(Math.random()*621));
			enemyList.add(enemy);
		}
	}
	
	private void enemyMoveProcess() {
		for(int i = 0; i <enemyList.size(); i++) {
			enemy = enemyList.get(i);
			enemy.move();
		}
	}
	
	private void enemyAttackProcess() {
		if(cnt % 50 == 0){
			enemyAttack = new EnemyAttack(enemy.x - 79, enemy.y+35);
			enemyAttackList.add(enemyAttack);
		}
		
		for (int i=0; i< enemyAttackList.size(); i++) {
			enemyAttack = enemyAttackList.get(i);
			enemyAttack.fire();			
		
			if(enemyAttack.x > playerX && enemyAttack.x < playerX + playerWidth && enemyAttack.y > playerY && enemyAttack.y < playerY+playerHeigth) {
				playerHp -= enemyAttack.attack;
				enemyAttackList.remove(enemyAttack);
				if(playerHp <= 0) {
					isOver = true;
				}
			}
		}		
	}
	
	public void gameDraw(Graphics g) {
		playerDraw(g);
		enemyDraw(g);
		infoDraw(g);
	}
	public void infoDraw(Graphics g) {
		g.setColor(Color.BLACK);
		g.setFont(new Font("Arial", Font.BOLD, 40));
		g.drawString("SCORE : " + score, 40, 80);
		if(isOver) {
			g.setFont(new Font("Arial", Font.BOLD, 80));
			g.drawString("Press R to restart : " + score, 40, 80);
		}
	}
	
	public void playerDraw(Graphics g) {
		g.drawImage(player, playerX, playerY, null);		
		g.setColor(Color.GREEN);
		g.fillRect(playerX - 1, playerY - 40,  playerHp * 6, 20);
		
		for(int i = 0; i < playerAttackList.size(); i++) {
			playerAttack = playerAttackList.get(i);
			g.drawImage(playerAttack.image, playerAttack.x, playerAttack.y, null);		
		}		
	}
	
	public void enemyDraw(Graphics g) {
		for (int i=0; i<enemyList.size(); i++) {
			enemy = enemyList.get(i);
			g.drawImage(enemy.image, enemy.x, enemy.y, null);
			g.setColor(Color.RED);
			g.fillRect(enemy.x + 1, enemy.y - 0,  enemy.hp * 15, 20);
		}
		
		for (int i=0; i<enemyAttackList.size(); i++) {
			enemyAttack = enemyAttackList.get(i);
			g.drawImage(enemyAttack.image, enemyAttack.x, enemyAttack.y, null);
		}
	}
	
	
	
	public boolean isOver() {
		return isOver;
	}


	public void setUp(boolean up) {
		this.up = up;
	}

	public void setDown(boolean down) {
		this.down = down;
	}

	public void setLeft(boolean left) {
		this.left = left;
	}

	public void setRight(boolean right) {
		this.right = right;
	}
	public void setShooting(boolean shooting) {
		this.shooting = shooting;
	}

}

 