package com.typefy.springboot.app.controllers.util.paginator;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;

public class PageRender<T>
{
	private String url;
	private Page<T> page;
	private int totalPages;
	private int elementsForPage;
	private int actualPage;
	
	private List<PageItem> pages;
	
	public PageRender(String url, Page<T> page) {
		this.url = url;
		this.page = page;
		this.pages = new ArrayList<PageItem>();
		
		elementsForPage = page.getSize();
		totalPages = page.getTotalPages();
		actualPage = page.getNumber() + 1;
		
		int from;
		int to;
		if( totalPages <= elementsForPage )
		{
			from = 1;
			to = totalPages;
		}
		else
		{
			if( actualPage <= elementsForPage/2 )
			{
				from = 1;
				to = elementsForPage;
			}
			else if( actualPage >= totalPages - elementsForPage/2 )
			{
				from = totalPages - elementsForPage+1;
				to = elementsForPage;
			}
			else
			{
				from = actualPage - elementsForPage/2;
				to = elementsForPage;
			}
		}
		
		for( int i = 0; i < to; i++ )
		{
			pages.add( new PageItem( from + i, actualPage == from + i) );
		}
	}

	public String getUrl() {
		return url;
	}

	public int getTotalPages() {
		return totalPages;
	}

	public int getActualPage() {
		return actualPage;
	}

	public List<PageItem> getPages() {
		return pages;
	}
	
	public boolean isFirst() {
		return page.isFirst();
	}
	
	public boolean isLast() {
		return page.isLast();
	}
	
	public boolean isHasNext() {
		return page.hasNext();
	}
	
	public boolean isHasPrevious() {
		return page.hasPrevious();
	}
}
