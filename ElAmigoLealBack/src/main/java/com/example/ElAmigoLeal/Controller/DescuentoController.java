package com.example.ElAmigoLeal.Controller;

import java.io.FileInputStream;
import java.io.IOException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.ElAmigoLeal.Entity.Descuento;
import com.example.ElAmigoLeal.Impl.DescuentoService;
import com.example.ElAmigoLeal.Utilities.ListarDescuentoExcel;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@RestController
@CrossOrigin(origins = "*", methods = { RequestMethod.PUT, RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE })
@RequestMapping(path = "/api")
public class DescuentoController {
	@Autowired
	private DescuentoService descuentoService;
	
	@GetMapping("/descuento")//YA
	public String listarDescuento(Model model) {
		model.addAttribute("descuento", descuentoservice.listarTodosLosDescuentos());
		return "Descuentos/Descuento";
	}
	
	@GetMapping ("/descuento/crear")
	public String crearDescuento(Model model) {
		Descuento descuento = new Descuento ();
		model.addAttribute("descuento", descuento );
		return "Descuentos/crear";
	}
	@PostMapping ("/descuento")
	public String guardarDescuento(@ModelAttribute("descuento") Descuento descuento) {
		descuentoservice.guardarDescuento(descuento);
		return "redirect:/descuento";
	}
	@GetMapping("/descuento/editar/{iddescuento}")
		public String editarDescuento(@PathVariable Integer iddescuento, Model model) {
		model.addAttribute("descuento", descuentoservice.obtenerDescuentobyId(iddescuento));
		return "Descuentos/editar";
	}
	
	@PostMapping ("/descuento/{iddescuento}")
	public String actualizarDescuento(@PathVariable Integer iddescuento, @ModelAttribute("descuento") Descuento descuento , Model model) {
		Descuento descuentoExistente = descuentoservice.obtenerDescuentobyId(iddescuento);
		descuentoExistente.setIddescuento(iddescuento);
		descuentoExistente.setValordescuento(descuento.getValordescuento());
		descuentoExistente.setFechadescuento(descuento.getFechadescuento());
		
		descuentoservice.actualizarDescuento(descuentoExistente);
		return "redirect:/descuento";
	}
	@GetMapping("/descuento/{iddescuento}")
	public String eliminarDescuento(@PathVariable Integer iddescuento) {
		descuentoservice.eliminarDescuento(iddescuento);
		return "redirect:/descuento";
	}
	@GetMapping("/exportarExcelDescuento")
	public void exportarListaDeRolExcel(HttpServletResponse response)throws IOException {
		response.setContentType("aplication/octec-stream");
		
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
		String fechaActual = dateFormatter.format(new Date());
		
		String cabecera = "Content-Disposition";
		String valor = "attachment; filename=Descuento_" + fechaActual + ".xlsx";
		
		response.setHeader(cabecera, valor);
		
		List<Descuento> descuento = descuentoService.findAll();
		
		ListarDescuentoExcel exporter = new ListarDescuentoExcel(descuento);
		exporter.Exportar(response);
	}
	@GetMapping("/ExportarPdfDescuento")
	public ResponseEntity<byte[]> generatePdf() throws Exception, JRException {
		
		    JRBeanCollectionDataSource beanCollectionDataSource=new JRBeanCollectionDataSource(descuentoService.findAll());
		    JasperReport compileReport = JasperCompileManager.compileReport(new FileInputStream("src/main/resources/MyReports/ReporteDescuentos.jrxml"));
		    
		    HashMap<String, Object> map=new HashMap<>();
		    JasperPrint report = JasperFillManager.fillReport(compileReport, null, beanCollectionDataSource);		    
		    byte[] data = JasperExportManager.exportReportToPdf(report);
		    
		    HttpHeaders headers=new HttpHeaders();
		    headers.set(HttpHeaders.CONTENT_DISPOSITION, "incline;filename=ReporteDescuento.pdf");
		    return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);
	}

}
