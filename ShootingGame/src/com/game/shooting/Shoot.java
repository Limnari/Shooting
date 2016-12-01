package com.game.shooting;

import java.awt.*;
import java.awt.event.*;//KeyListener를 사용하기 위해 필요(implements로 interface 상속받은것 중에서)
import java.awt.image.*;
import java.util.ArrayList;
import javax.swing.*;//JFrame을 사용하기 위해선 필요
//GUI를 구현하려면 JFrame을 상속받아야 함
//KeyListener 키보드 누르거나 띨때 발생하는 이벤트때 구현되는것
//Runnable interface 쓰레드를 만들기 위해 implements로 상속받음

public class Shoot extends JFrame implements Runnable, KeyListener {
	//변수 선언만 함
	private BufferedImage bi = null;
	private ArrayList msList = null;//ArrayList 객체 담을 참조 변수 선언
	private ArrayList enList = null;//ArrayList 객체 담을 참조 변수 선언
	private boolean left = false, right = false, up = false, down = false, fire = false;
	//방향,발사 키 기본 설정값인듯
	private boolean start = false, end = false;
	//수치가 나오면 알아보기 위해 바꿔보면 바로 알 수 있음 안되면 할 수 없고
	private int w = 300, h = 500, x = 130, y = 450, xw = 20, xh = 20;
	//w 창의 너비 / h 창 높이 / x 플레이어 x좌표 / y 플레이어 y좌표 / xw 플레이어 너비 / xh 플레이어 높이 ???
	
	
	public Shoot() {//shoot클래스 기본생성자 객체를 생성할때 초기화한다
		//뭔가 새로 만들어질때 기본적으로 초기화하는 작업
		
		bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		msList = new ArrayList();//ArrayList를 이용해 객체를 하나 만듬
		enList = new ArrayList();//ArrayList를 이용해 객체를 하나 만듬
		this.addKeyListener(this);
		this.setSize(w, h);//프레임 크기 x, y
		this.setTitle("Shooting Game");//창 제목
		this.setResizable(false);//인자값은 boolean type을 받는 메소드임 창크기 변경불변 true면 창 크기 변경 됨
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//이걸 설정 안하면 창만 꺼지고 작업관리자 프로세스에보면 javax가 계속 실행되고 있음
		//마치 컴퓨터 모니터만 끄고 본체는 끄지 않은 것과 마찬가지임
		//창과 프로그램을 동시에 종료시켜줌
		//예를 들어 백신프로그램은 창만 꺼지고 프로그램은 계속 진행되는 것
		this.setVisible(true);//창을 보여주려면 true, false는 NullPointException 예외사항 나옴
	}

	public void run() {
		try {
			int msCnt = 0;
			int enCnt = 0;
			while (true) {
				Thread.sleep(10);//인자값만큼 쓰레드가 멈춰있음 게임 전체 속도조절 역할이 됨

				if (start) {//start가 참이면 아래 if문으로
					if (enCnt > 200) {//enCnt가 2000보다 크면 enCreate메소드 호출하고 다시 enCnt는 0이 됨
						enCreate();
						enCnt = 0;
					}
					if (msCnt >= 100) {
						fireMs();
						msCnt = 0;
					}
					msCnt += 10;
					enCnt += 10;
					keyControl();
					crashChk();
				}
				draw();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void fireMs() {
		if (fire) {
			if (msList.size() < 100) {
				Ms m = new Ms(this.x, this.y);
				msList.add(m);
			}
		}
	}

	public void enCreate() {
		for (int i = 0; i < 9; i++) {//9번 반복
			double rx = Math.random() * (w - xw);//로컬변수 지역변수
			double ry = Math.random() * 50;
			Enemy en = new Enemy((int) rx, (int) ry);//적 Enemy가 나옴
			enList.add(en);
		}
	}

	public void crashChk() {
		Graphics g = this.getGraphics();
		Polygon p = null;
		for (int i = 0; i < msList.size(); i++) {
			Ms m = (Ms) msList.get(i);
			for (int j = 0; j < enList.size(); j++) {
				Enemy e = (Enemy) enList.get(j);
				int[] xpoints = { m.x, (m.x + m.w), (m.x + m.w), m.x };
				int[] ypoints = { m.y, m.y, (m.y + m.h), (m.y + m.h) };
				p = new Polygon(xpoints, ypoints, 4);
				if (p.intersects((double) e.x, (double) e.y, (double) e.w, (double) e.h)) {
					msList.remove(i);
					enList.remove(j);
				}
			}
		}
		for (int i = 0; i < enList.size(); i++) {
			Enemy e = (Enemy) enList.get(i);
			int[] xpoints = { x, (x + xw), (x + xw), x };
			int[] ypoints = { y, y, (y + xh), (y + xh) };
			p = new Polygon(xpoints, ypoints, 4);
			if (p.intersects((double) e.x, (double) e.y, (double) e.w, (double) e.h)) {
				enList.remove(i);
				start = false;
				end = true;
			}
		}
	}

	public void draw() {
		Graphics gs = bi.getGraphics();
		gs.setColor(Color.white);
		gs.fillRect(0, 0, w, h);
		gs.setColor(Color.black);
		gs.drawString("Enemy 객체수 : " + enList.size(), 180, 50);
		gs.drawString("Ms 객체수 : " + msList.size(), 180, 70);
		gs.drawString("게임시작 : Enter", 180, 90);

		if (end) {
			gs.drawString("G A M E     O V E R", 100, 250);
		}

		gs.fillRect(x, y, xw, xh);

		for (int i = 0; i < msList.size(); i++) {
			Ms m = (Ms) msList.get(i);
			gs.setColor(Color.blue);
			gs.drawOval(m.x, m.y, m.w, m.h);
			if (m.y < 0)
				msList.remove(i);
			m.moveMs();
		}
		gs.setColor(Color.black);
		for (int i = 0; i < enList.size(); i++) {
			Enemy e = (Enemy) enList.get(i);
			gs.fillRect(e.x, e.y, e.w, e.h);
			if (e.y > h)
				enList.remove(i);
			e.moveEn();
		}

		Graphics ge = this.getGraphics();
		try{
		ge.drawImage(bi, 0, 0, w, h, this);
		}catch(java.lang.NullPointerException e){
			e.printStackTrace();
		}
	}

	public void keyControl() {
		if (0 < x) {
			if (left)
				x -= 3;
		}
		if (w > x + xw) {
			if (right)
				x += 3;
		}
		if (25 < y) {
			if (up)
				y -= 3;
		}
		if (h > y + xh) {
			if (down)
				y += 3;
		}
	}

	public void keyPressed(KeyEvent ke) {
		switch (ke.getKeyCode()) {
		case KeyEvent.VK_LEFT:
			left = true;
			break;
		case KeyEvent.VK_RIGHT:
			right = true;
			break;
		case KeyEvent.VK_UP:
			up = true;
			break;
		case KeyEvent.VK_DOWN:
			down = true;
			break;
		case KeyEvent.VK_A:
			fire = true;
			break;
		case KeyEvent.VK_ENTER:
			start = true;
			end = false;
			break;
		}
	}

	public void keyReleased(KeyEvent ke) {
		switch (ke.getKeyCode()) {
		case KeyEvent.VK_LEFT:
			left = false;
			break;
		case KeyEvent.VK_RIGHT:
			right = false;
			break;
		case KeyEvent.VK_UP:
			up = false;
			break;
		case KeyEvent.VK_DOWN:
			down = false;
			break;
		case KeyEvent.VK_A:
			fire = false;
			break;
		}
	}

	public void keyTyped(KeyEvent ke) {
	}

	public static void main(String[] args) {
		Thread t = new Thread(new Shoot());//쓰레드 하나 생성, 클래스와 같은 이름을 쓰는 생성자 Shoot클래스를 가지고 쓰레드만드는....
		t.start();//쓰레드 시작하기
	}
}

class Ms {
	int x;
	int y;
	int w = 5;
	int h = 5;

	public Ms(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public void moveMs() {
		y--;
	}
}

class Enemy {
	int x;
	int y;
	int w = 10;
	int h = 10;

	public Enemy(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public void moveEn() {
		y++;
	}
}