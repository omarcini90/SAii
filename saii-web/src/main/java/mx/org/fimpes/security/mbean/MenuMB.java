package mx.org.fimpes.security.mbean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSeparator;
import org.primefaces.model.menu.DefaultSubMenu;
import org.primefaces.model.menu.MenuModel;

import mx.org.fimpes.saii.model.security.Menus;

/**
*
* @author Universidad Latinoamericana | Omar.Rendon@ula.edu.mx | omar.josue@gmail.com
*/
@Named(value = "menuMB")
@SessionScoped
public class MenuMB implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(MenuMB.class);

    private MenuModel model;
    
    @Inject
    LoginMB loginMB;
    
    /**
     * Creates a new instance of MenuMB
     */
    public MenuMB() {
    }

    @PostConstruct
    public void init() {
        configureMenu();
    }
    
    @PreDestroy
    public void destroy() {
        loginMB = null;
        model = null;
    }

    private Set<Menus> getUsersMenu() {
        // Roles contain authenticated users' menus
        var r = loginMB.getRoles();
        final Set<Menus> menu = new HashSet<Menus>();
        if (r != null) {
            log.info("Retrieving menu from user profile..."); 
            // We guarantee no duplicated menus: 
            r.stream().map((rol) -> rol.getMenusList()).forEach((cm) -> {
                if (menu.addAll(cm)) {
                    log.info("Accumuled menu: " + menu.size() + " items");
                }
            });
        }
        return menu;
    }

    private void configureMenu() {
        Set<Menus> menu = getUsersMenu();
        Map<Integer, DefaultSubMenu> modulos = new HashMap<Integer, DefaultSubMenu>();
        // We first build the Submenu tree (Modules):
        menu.stream().map((item) -> item.getIdModulo()).filter((m)
                -> (!modulos.containsKey(m.getIdModulo()))).forEach((m) -> {
            modulos.put(m.getIdModulo(), new DefaultSubMenu(m.getEtiquetaMenu()));
        });
        // Discarding inactive items (Menus):
        menu.stream().forEach((m) -> {
            if (m.getActivo()) {
                Integer modId = m.getIdModulo().getIdModulo();
                DefaultSubMenu sm = modulos.get(modId);
                // Creating MenuItems from Menus:
                var mi = new DefaultMenuItem(m.getEtiquetaMenu());
                mi.setIcon(m.getIcono());
                mi.setAjax(false);                
                var url = m.getUrl();                
                if(url.startsWith("http")){
                    mi.setUrl(m.getUrl());
                }else{
                    mi.setOutcome(m.getUrl());
                }
                sm.addElement(mi);
                modulos.put(modId, sm);
                log.info(m.getUrl());
            }else{
                log.info("Inactive -> " + m.getUrl());
            }
        });

        buildModel(modulos);
    }
    
    private void buildModel(Map<Integer, DefaultSubMenu> modulos) {
        model = new DefaultMenuModel();
        log.info("Building active menu...");
        // Home:
        var home = new DefaultSubMenu("Inicio");
        home.setIcon("fa fa-home");
        
        // Dashboard:
        var index = new DefaultMenuItem("Indicadores");
        index.setIcon("fa fa-line-chart");
        index.setOutcome("/index");
        home.addElement(index);        
        // Reports:
        var report = new DefaultMenuItem("Reportes");
        report.setIcon("fa fa-table");
        report.setOutcome("/reportes/consulta");
        home.addElement(report);
        // Load:
        /*var load = new DefaultMenuItem("Cargar layout");
        load.setIcon("fa fa-upload");
        load.setOutcome("/proceso/carga?init=false");
        home.addElement(load);
        // Download:
        var download = new DefaultMenuItem("Descargar layout");
        download.setIcon("fa fa-download");
        download.setAjax(false);
        download.setCommand("#{layoutController.download}");
        home.addElement(download);
        */
        // Manuals:
        var doc = new DefaultSubMenu("Manuales");
        doc.setIcon("fa fa-file-archive-o");
        
        if(loginMB.isFimpesRol()) {
        	var admon = new DefaultMenuItem("Administrativo");
            admon.setIcon("fa fa-book");
            admon.setOnclick("PF('AdminManualDialog').show();");
            doc.addElement(admon);
        }
        
        var op = new DefaultMenuItem("Operativo");
        op.setIcon("fa fa-book");
        op.setOnclick("PF('OpManualDialog').show();");
        doc.addElement(op);
        
        home.addElement(doc);        
        // About:
        var about = new DefaultMenuItem("Acerca de");
        about.setIcon("fa fa-window-maximize");
        about.setOnclick("PF('AboutDialog').show();");        
        home.addElement(about);
        
        // Sign out:
        var signout = new DefaultMenuItem("Cerrar sesión");
        signout.setIcon("fa fa-power-off");
        signout.setOutcome("/logout");
        home.addElement(new DefaultSeparator());
        home.addElement(signout);        
        
        model.addElement(home);
        // Dynamic items:
        modulos.values().stream().forEach((sm) -> {
            model.addElement(sm);
        });        
        model.generateUniqueIds();
        log.info("Successfully built!");
    }

    public MenuModel getMenuModel() {
        return model;
    }
}
