package net.wurmunlimitedtools;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

import net.wurmunlimitedtools.filter.AverageFilter;
import net.wurmunlimitedtools.filter.BiomeFilter;
import net.wurmunlimitedtools.filter.DeltaFilter;
import net.wurmunlimitedtools.filter.EdgeFilter;
import net.wurmunlimitedtools.filter.FeatureMountainFilter;
import net.wurmunlimitedtools.filter.ForestFilter;
import net.wurmunlimitedtools.filter.GrassDecoratorFilter;
import net.wurmunlimitedtools.filter.OceanFilter;
import net.wurmunlimitedtools.filter.MountainFilter;
import net.wurmunlimitedtools.filter.NoiseFilter;
import net.wurmunlimitedtools.filter.OreFilter;
import net.wurmunlimitedtools.filter.ResourceFilter;
import net.wurmunlimitedtools.filter.RidgeFilter;
import net.wurmunlimitedtools.filter.RiverFilter;
import net.wurmunlimitedtools.filter.RockDecoratorFilter;
import net.wurmunlimitedtools.filter.RockLayerFilter;
import net.wurmunlimitedtools.filter.SmoothCoastFilter;
import net.wurmunlimitedtools.filter.TarPitFilter;

public final class Terragen {
	public static void generate(int size, long seed, JLabel label, JProgressBar progress, JButton button, String name) throws IOException {
		label.setText(" Generating " + size + "x" + size + " map with seed " + seed);
		Map map = new Map(size, seed);
		progress.setValue(1);

		new DeltaFilter().apply(map);
		progress.setValue(5);
		label.setText(" Generating mountains...");
		new MountainFilter(size >= 4096).apply(map);
		progress.setValue(22);
		label.setText(" Generating ridged mountains...");
		new RidgeFilter(size >= 4096).apply(map);
		progress.setValue(42);
		label.setText(" Generating noise...");
		new NoiseFilter().apply(map);
		progress.setValue(52);
		label.setText(" Generating oceans...");
		new OceanFilter(size <= 2048).apply(map);
		progress.setValue(55);
		label.setText(" Smoothing terrain...");
		new AverageFilter().apply(map);
		progress.setValue(60);
		label.setText(" Generating rives and lakes...");
		new RiverFilter().apply(map);
		progress.setValue(65);
		if (size >= 4096) {
			label.setText(" Generating peaks...");
			new FeatureMountainFilter().apply(map);
		}
		progress.setValue(70);
		label.setText(" Lowering coastlines...");
		new EdgeFilter(size <= 2048 ? 128 : 256).apply(map);
		progress.setValue(75);
		label.setText(" Smoothing coastlines...");
		new SmoothCoastFilter().apply(map);
		progress.setValue(80);
		label.setText(" Generating rock layer...");
		new RockLayerFilter().apply(map);
		progress.setValue(82);
		label.setText(" Generating rocks and cliffs...");
		new RockDecoratorFilter().apply(map);
		progress.setValue(84);
		label.setText(" Generating deserts...");
		new BiomeFilter(1, 4639).apply(map);
		progress.setValue(86);
		label.setText(" Generating tundra...");
		new BiomeFilter(19, 3748).apply(map);
		progress.setValue(88);
		label.setText(" Generating steppe...");
		new BiomeFilter(22, 8178).apply(map);
		progress.setValue(90);
		label.setText(" Generating clay...");
		new ResourceFilter(6, 6578).apply(map);
		progress.setValue(91);
		label.setText(" Generating peat...");
		new ResourceFilter(18, 8739).apply(map);
		progress.setValue(92);
		label.setText(" Generating moss...");
		new ResourceFilter(20, 1043).apply(map);
		progress.setValue(93);
		label.setText(" Generating grass and flowers...");
		new GrassDecoratorFilter().apply(map);
		progress.setValue(94);
		label.setText(" Generating tar pits...");
		new TarPitFilter().apply(map);
		progress.setValue(95);
		label.setText(" Generating forests...");
		new ForestFilter().apply(map);
		progress.setValue(96);
		label.setText(" Generating ores...");
		new OreFilter().apply(map);
		progress.setValue(97);

		label.setText(" Rendering map...");
		BufferedImage img = map.getSurface().render();
		progress.setValue(99);

		Path dir = Paths.get("./map_" + name);
		if (!Files.isDirectory(dir)) {
			Files.createDirectory(dir);
		}

		progress.setValue(100);
		label.setText(" Saving...");
		try (OutputStream out = new BufferedOutputStream(Files.newOutputStream(dir.resolve("render.png")), 65536)) {
			ImageIO.write(img, "PNG", out);
		}
		map.save(dir);

		label.setText(" Done. Saved to map_" + name + ".");
		button.setEnabled(true);
		progress.setValue(0);
	}
}
