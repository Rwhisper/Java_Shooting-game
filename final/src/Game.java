import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;

import javax.swing.ImageIcon;

public class Game extends Thread {
	private int delay = 20; //������ ������
	private long pretime;
	private int cnt; 	//�����̸��� ������ cnt (�̺�Ʈ �߻� �ֱ�)
	
	private Image player = new ImageIcon("src/img/en.png").getImage();
	
	private int playerX, playerY;
	private int playerWidth = player.getWidth(null);	//�÷��̾��� x��
	private int playerHeigth = player.getHeight(null);	//�÷��̾��� y��
	private int playerSpeed = 10;	//�ѹ��� ������ �Ÿ�
	private int playerHp = 30;		//�÷��̾��� ü��
	private int score;
	private boolean up, down, left, right, shooting;	//�÷��̾��� �������� ����
	private boolean isOver;				//�������� �ȳ������� Ȯ����
	
	private ArrayList<PlayerAttack> playerAttackList = new ArrayList<PlayerAttack>();
	private ArrayList<Enemy> enemyList = new ArrayList<Enemy>();
	private ArrayList<EnemyAttack> enemyAttackList = new ArrayList<EnemyAttack>();
	
	private PlayerAttack playerAttack;	//�÷��̾� �����̶�� Ŭ������ �����ͼ� ��ü������ ����
	private Enemy enemy;				//�÷��̾� �����̶�� Ŭ������ �����ͼ� ��ü������ ����
	private EnemyAttack enemyAttack;	//���ʹ̾����̶�� Ŭ������ �����ͼ� ��ü������ ����
	
	@Override
	public void run() {		
		reset();
		while(true) {	//delay�� ���������� cnt����
			while(!isOver) {
				pretime = System.currentTimeMillis();	//��Ȯ�� �ֱ⸦ ����
				if(System.currentTimeMillis() - pretime < delay) {	//����ð� - (cnt�� �����ϱ� ���ð�) < delay�� ��� 	
					try {
						Thread.sleep(delay - System.currentTimeMillis() + pretime);	//�� ���̸�ŭ �����忡 seelp
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
		//ȭ��Ʒ��� �÷��̾ ������
		if(up && playerY - playerSpeed > 0) playerY -= playerSpeed;		
		//ȭ�� ���� �÷��̾ �ö� �� �÷��̾��� ������ġ + �̵��� ��ġ +������ ũ�Ⱑ ����ȭ�麸�� ���������� ����(ȭ����� �ȳѾ�� ���ش�)
		if(down && playerY + playerHeigth + playerSpeed < Main.SCREEEN_HEIGHT) playerY += playerSpeed;	
		//ȭ��������� �÷��̾ �����δ�.
		if(left && playerX - playerSpeed > 0) playerX -= playerSpeed;
		//ȭ�� ���������� �÷��̾ �����δ� �� �÷��̾��� ������ġ + �̵��� ��ġ +������ ũ�Ⱑ ����ȭ�麸�� ���������� ����(ȭ����� �ȳѾ�� ���ش�)
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

 