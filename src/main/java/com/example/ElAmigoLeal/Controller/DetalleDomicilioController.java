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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.ElAmigoLeal.Entity.DetalleDomicilio;
import com.example.ElAmigoLeal.Entity.Domicilio;
import com.example.ElAmigoLeal.Impl.DetalleDomicilioService;
import com.example.ElAmigoLeal.Repository.DomicilioRepository;
import com.example.ElAmigoLeal.Utilities.ListarDetalleDomicilioExcel;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Controller
public class DetalleDomicilioController {

	@Autowired
	private DetalleDomicilioService detalledomicilioservice;
	
	@Autowired
	private DomicilioRepository domiciliorepository;
	
	@GetMapping("/detalledomicilio")//YA
	public String listarDetalledomicilio(Model model) {
		model.addAttribute("detalledomicilio", detalledomicilioservice.listarTodosLosDetalleDomicilios());
		return "DetalleDomicilios/DetalleDomicilio";
	}
	
	@GetMapping ("/detalledomicilio/crear")
	public String crearDetalleDomicilio(Model model) {
		DetalleDomicilio detalledomicilio = new DetalleDomicilio ();
		List<Domicilio> listarTodosLosDomicilios = domiciliorepository.findAll();
		
		model.addAttribute("detalledomicilio", detalledomicilio );
		model.addAttribute("listarTodosLosDomicilios", listarTodosLosDomicilios);
		return "DetalleDomicilios/crear";
	}
	@PostMapping ("/detalledomicilio")
	public String guardarDetalleDomicilio(@ModelAttribute("detalledomicilio") DetalleDomicilio detalledomicilio) {
		detalledomicilioservice.guardarDetalleDomicilio(detalledomicilio);
		return "redirect:/detalledomicilio";
	}
	@GetMapping("/detalledomicilio/editar/{id}")
		public String editarDetalleDomicilio(@PathVariable Integer id, Model model) {
		model.addAttribute("detalledomicilio", detalledomicilioservice.obtenerDetalleDomiciliobyId(id));
		
		List<Domicilio> listarTodosLosDomicilios = domiciliorepository.findAll();
		model.addAttribute("listarTodosLosDomicilios", listarTodosLosDomicilios);
		return "DetalleDomicilios/editar";
	}
	
	@PostMapping ("/detalledomicilio/{id}")
	public String actualizarDetalleDomicilio(@PathVariable Integer id, @ModelAttribute("detalledomicilio") DetalleDomicilio detalledomicilio , Model model) {
		DetalleDomicilio detalledomicilioExistente = detalledomicilioservice.obtenerDetalleDomiciliobyId(id);
		detalledomicilioExistente.setId(id);
		detalledomicilioExistente.setDomicilio(detalledomicilio.getDomicilio());
		detalledomicilioExistente.setEstado(detalledomicilio.getEstado());
		detalledomicilioExistente.setDireccion(detalledomicilio.getDireccion());
		detalledomicilioExistente.setTelefono(detalledomicilio.getTelefono());
		
		detalledomicilioservice.actualizarDetalleDomicilio(detalledomicilioExistente);
		return "redirect:/detalledomicilio";
	}
	@GetMapping("/detalledomicilio/{id}")
	public String eliminarDetalleDomicilio(@PathVariable Integer id) {
		detalledomicilioservice.eliminarDetalleDomicilio(id);
		return "redirect:/detalledomicilio";
	}
	@GetMapping("/exportarExcelDetalle")
	public void exportarListaDeRolExcel(HttpServletResponse response)throws IOException {
		response.setContentType("aplication/octec-stream");
		
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
		String fechaActual = dateFormatter.format(new Date());
		
		String cabecera = "Content-Disposition";
		String valor = "attachment; filename=DetalleDomicilio_" + fechaActual + ".xlsx";
		
		response.setHeader(cabecera, valor);
		
		List<DetalleDomicilio> detalledomicilio = detalledomicilioservice.listarTodosLosDetalleDomicilios();
		
		ListarDetalleDomicilioExcel exporter = new ListarDetalleDomicilioExcel(detalledomicilio);
		exporter.Exportar(response);
	}
	
	@GetMapping("/ExportarPdfDetalle")
	public ResponseEntity<byte[]> generatePdf() throws Exception, JRException {
		
		    JRBeanCollectionDataSource beanCollectionDataSource=new JRBeanCollectionDataSource(detalledomicilioservice.listarTodosLosDetalleDomicilios());
		    JasperReport compileReport = JasperCompileManager.compileReport(new FileInputStream("src/main/resources/MyReports/ReporteDetalleDomicilio.jrxml"));
		    
		    HashMap<String, Object> map=new HashMap<>();
		    JasperPrint report = JasperFillManager.fillReport(compileReport, null, beanCollectionDataSource);		    
		    byte[] data = JasperExportManager.exportReportToPdf(report);
		    
		    HttpHeaders headers=new HttpHeaders();
		    headers.set(HttpHeaders.CONTENT_DISPOSITION, "incline;filename=ReporteDetalleDomicilio.pdf");
		    return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);
	}
	
}
