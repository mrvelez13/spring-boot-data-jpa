package com.typefy.springboot.app.controllers.util.paginator;

public class PageItem
{
	private int numPage;
	private boolean actualPage;

	public PageItem(int numPage, boolean actualPage)
	{
		this.numPage = numPage;
		this.actualPage = actualPage;
	}

	public int getNumPage() {
		return numPage;
	}

	public void setNumPage(int numPage) {
		this.numPage = numPage;
	}

	public boolean isActualPage() {
		return actualPage;
	}

	public void setActualPage(boolean actualPage) {
		this.actualPage = actualPage;
	}
}
