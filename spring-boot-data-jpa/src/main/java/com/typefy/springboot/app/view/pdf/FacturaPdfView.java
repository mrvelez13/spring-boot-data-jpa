package com.typefy.springboot.app.view.pdf;

import java.awt.Color;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractPdfView;

import com.lowagie.text.Document;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfCell;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.typefy.springboot.app.models.entity.DetalleFactura;
import com.typefy.springboot.app.models.entity.Factura;

@Component("factura/detalle")
public class FacturaPdfView extends AbstractPdfView
{

	@Override
	protected void buildPdfDocument(Map<String, Object> model, Document document, PdfWriter writer,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		Factura factura = (Factura) model.get("factura");
		
		PdfPTable table = new PdfPTable(1);
		table.setSpacingAfter(20);
		
		PdfPCell cell = null;
				
		cell = new PdfPCell(new Phrase("Datos del cliente"));
		cell.setBackgroundColor(new Color(184, 218, 255));
		cell.setPadding(8f);
		
		table.addCell(cell);
		table.addCell(factura.getCliente().getNombre() + " " + factura.getCliente().getApellido());
		table.addCell(factura.getCliente().getEmail());
		
		PdfPTable table2 = new PdfPTable(1);
		table2.setSpacingAfter(20);
		
		cell = new PdfPCell(new Phrase("Datos de la factura"));
		cell.setBackgroundColor(new Color(195, 230, 203));
		cell.setPadding(8f);
		
		table2.addCell(cell);
		table2.addCell("Folio " + factura.getId());
		table2.addCell("Descripción " + factura.getDescripcion());
		table2.addCell("Fecha " + factura.getCreateAt());
		
		document.add(table);
		document.add(table2);
		
		PdfPTable table3 = new PdfPTable(4);
		table3.setWidths(new float[] {3.5f,1,1,1});
		table2.addCell("Producto");
		table2.addCell("Precio");
		table2.addCell("Cantidad");
		table2.addCell("Total");
		
		for(DetalleFactura detalle: factura.getDetalleFactura())
		{
			table3.addCell(detalle.getProducto().getNombre());
			table3.addCell(detalle.getProducto().getPrecio().toString());
			
			cell = new PdfPCell(new Phrase(detalle.getCantidad().toString()));
			cell.setHorizontalAlignment(PdfCell.ALIGN_CENTER);
			table3.addCell(cell);
			table3.addCell(detalle.calcularImporte().toString());
		}
		
		cell = new PdfPCell(new Phrase("Total: "));
		cell.setColspan(3);
		cell.setHorizontalAlignment(PdfCell.ALIGN_RIGHT);
		
		table3.addCell(cell);
		table3.addCell(factura.getTotal().toString());
		
		document.add(table3);
		
	}
	
}
