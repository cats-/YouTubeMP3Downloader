package cats.ytmp3.gui.comp.queue;

import cats.ytmp3.dl.Downloader;
import cats.ytmp3.dl.State;
import cats.ytmp3.dl.event.DownloadEvent;
import cats.ytmp3.dl.event.DownloadListener;
import cats.ytmp3.dl.event.DownloadServiceEvent;
import cats.ytmp3.dl.event.DownloadServiceListener;
import cats.ytmp3.dl.util.DownloaderUtil;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: cats
 * Date: 6/21/13
 * Time: 10:18 AM
 */
public class TableQueue extends JTable implements DownloadServiceListener, DownloadListener {

    private static class TableColumnContext {

        private final String name;
        private final Class<?> clazz;
        private final int width;

        private TableColumnContext(final String name, final Class<?> clazz, final int width){
            this.name = name;
            this.clazz = clazz;
            this.width = width;
        }
    }

    private class ProgressRenderer extends DefaultTableCellRenderer {

        private final JProgressBar bar;

        private ProgressRenderer(){
            bar = new JProgressBar(JProgressBar.HORIZONTAL, 0, 100);
            bar.setValue(0);
            bar.setStringPainted(true);
            bar.setString("0%");
        }
        public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focused, int r, int c){
            if(r < 0 || r >= getRowCount())
                return bar;
            if(r < 0 || r >= model.map.size())
                return bar;
            final Object[] data = model.getData(r);
            final int progress = (Integer)data[c];
            bar.setValue(progress);
            bar.setString(progress + "%");
            return bar;
        }
    }

    private class LabelRenderer extends DefaultTableCellRenderer{

        private final JLabel label;
        private final JLabel icon;
        private final JPanel panel;

        private LabelRenderer(){
            label = new JLabel();
            label.setHorizontalAlignment(JLabel.CENTER);

            icon = new JLabel();
            icon.setBorder(new EmptyBorder(0, 5, 0, 5));

            panel = new JPanel(new BorderLayout());
            panel.add(icon, BorderLayout.WEST);
            panel.add(label, BorderLayout.CENTER);
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focused, int r, int c){
            if(r < 0 || r >= getRowCount())
                return panel;
            if(r < 0 || r >= model.map.size())
                return panel;
            final Downloader downloader = model.get(r);
            final Object[] data = model.map.get(downloader);
            final String string = (String)data[c];
            label.setText(string);
            label.setBackground(selected ? Color.GREEN : Color.RED);
            if(c == 0){
                icon.setIcon(downloader.getState().getIcon());
                panel.setToolTipText(downloader.getRequest().getVideo().toToolTip());
            }
            return panel;
        }
    }

    private class Model extends DefaultTableModel {

        private final Map<Downloader, Object[]> map = new HashMap<>();

        private void add(final Downloader downloader){
            final Object[] data = DownloaderUtil.create(downloader);
            map.put(downloader, data);
            addRow(data);
        }

        private void update(final Downloader downloader){
            final Object[] data = map.get(downloader);
            DownloaderUtil.update(downloader, data);
            map.put(downloader, data);
            fireTableDataChanged();
        }

        private Downloader get(final int index){
            return map.keySet().toArray(new Downloader[0])[index];
        }

        private Object[] getData(final int index){
            return map.get(get(index));
        }

        private void update(final int index){
            update(get(index));
        }

        private void remove(final Downloader downloader){
            final Downloader[] keys = map.keySet().toArray(new Downloader[0]);
            for(int i = 0; i < keys.length; i++){
                if(keys[i].equals(downloader)){
                    map.remove(downloader);
                    removeRow(i);
                    break;
                }
            }
        }

        private void removeAll(){
            while(getRowCount() > 0)
                removeRow(0);
        }

        private void filter(final State[] states){
            removeAll();
            final List<State> statesList = Arrays.asList(states);
            map.keySet().stream().filter(dler -> statesList.contains(dler.getState())).forEach(dler -> addRow(map.get(dler)));
        }

        public Class<?> getColumnClass(final int c){
            return CONTEXTS[c].clazz;
        }

        public int getColumnCount(){
            return CONTEXTS.length;
        }

        public String getColumnName(final int c){
            return CONTEXTS[c].name;
        }

        public boolean isCellEditable(final int r, final int c){
            return false;
        }
    }

    private static final TableColumnContext[] CONTEXTS = {
            new TableColumnContext("Name", String.class, 300),
            new TableColumnContext("Speed", String.class, 100),
            new TableColumnContext("Downloaded", String.class, 100),
            new TableColumnContext("Progress", Integer.class, 100),
            new TableColumnContext("Size", String.class, 100),
            new TableColumnContext("Remaining", String.class, 100),
            new TableColumnContext("Elapsed Time", String.class, 150),
            new TableColumnContext("Remaining Time", String.class, 150),
            new TableColumnContext("State", String.class, 100)
    };

    private final TableQueuePanel parent;
    private final Model model;

    public TableQueue(final TableQueuePanel parent){
        parent.getWindow().getService().addDownloadServiceListener(this);
        parent.getWindow().getService().addDownloadListener(this);
        this.parent = parent;
        model = new Model();
        setModel(model);
        setAutoResizeMode(AUTO_RESIZE_OFF);

        for(int i = 0; i < CONTEXTS.length; i++){
            getColumnModel().getColumn(i).setPreferredWidth(CONTEXTS[i].width);
            getColumnModel().getColumn(i).setCellRenderer(i == 3 ? new ProgressRenderer() : new LabelRenderer());
        }

        getTableHeader().setReorderingAllowed(false);
    }

    public void filter(final State[] states){
        model.filter(states);
    }

    public void onDownloadStart(final DownloadEvent e){
        final Downloader downloader = e.getDownloader();
        model.update(downloader);
    }

    public void onDownloadUpdate(final DownloadEvent e){
        final Downloader downloader = e.getDownloader();
        model.update(downloader);
    }

    public void onDownloadFinish(final DownloadEvent e){
        final Downloader downloader = e.getDownloader();
        model.update(downloader);
    }

    public void onDownloaderAdd(final DownloadServiceEvent e){
        final Downloader downloader = e.getDownloader();
        model.add(downloader);
    }

    public void onDownloaderRemove(final DownloadServiceEvent e){
        final Downloader downloader = e.getDownloader();
        model.remove(downloader);
    }
}
