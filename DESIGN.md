---
name: Editorial 1996
colors:
  surface: '#f9f9f9'
  surface-dim: '#dadada'
  surface-bright: '#f9f9f9'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f3f3f3'
  surface-container: '#eeeeee'
  surface-container-high: '#e8e8e8'
  surface-container-highest: '#e2e2e2'
  on-surface: '#1a1c1c'
  on-surface-variant: '#444748'
  inverse-surface: '#2f3131'
  inverse-on-surface: '#f1f1f1'
  outline: '#747878'
  outline-variant: '#c4c7c7'
  surface-tint: '#5f5e5e'
  primary: '#000000'
  on-primary: '#ffffff'
  primary-container: '#1c1b1b'
  on-primary-container: '#858383'
  inverse-primary: '#c8c6c5'
  secondary: '#5e5e5e'
  on-secondary: '#ffffff'
  secondary-container: '#e3e2e2'
  on-secondary-container: '#646464'
  tertiary: '#000000'
  on-tertiary: '#ffffff'
  tertiary-container: '#1c1b1a'
  on-tertiary-container: '#868382'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#e5e2e1'
  primary-fixed-dim: '#c8c6c5'
  on-primary-fixed: '#1c1b1b'
  on-primary-fixed-variant: '#474746'
  secondary-fixed: '#e3e2e2'
  secondary-fixed-dim: '#c7c6c6'
  on-secondary-fixed: '#1b1c1c'
  on-secondary-fixed-variant: '#464747'
  tertiary-fixed: '#e6e2df'
  tertiary-fixed-dim: '#cac6c4'
  on-tertiary-fixed: '#1c1b1a'
  on-tertiary-fixed-variant: '#484645'
  background: '#f9f9f9'
  on-background: '#1a1c1c'
  surface-variant: '#e2e2e2'
typography:
  display-brand:
    fontFamily: Playfair Display
    fontSize: 48px
    fontWeight: '400'
    lineHeight: 52px
    letterSpacing: -0.02em
  headline-lg:
    fontFamily: Playfair Display
    fontSize: 36px
    fontWeight: '400'
    lineHeight: 44px
    letterSpacing: -0.01em
  headline-lg-mobile:
    fontFamily: Playfair Display
    fontSize: 28px
    fontWeight: '400'
    lineHeight: 34px
    letterSpacing: -0.01em
  headline-md:
    fontFamily: Playfair Display
    fontSize: 22px
    fontWeight: '400'
    lineHeight: 28px
  headline-sm:
    fontFamily: Playfair Display
    fontSize: 18px
    fontWeight: '400'
    lineHeight: 24px
  body-lg:
    fontFamily: Inter
    fontSize: 15px
    fontWeight: '400'
    lineHeight: 22px
    letterSpacing: -0.005em
  body-md:
    fontFamily: Inter
    fontSize: 13px
    fontWeight: '400'
    lineHeight: 18px
  label-caps:
    fontFamily: Inter
    fontSize: 10px
    fontWeight: '500'
    lineHeight: 14px
    letterSpacing: 0.12em
  date-stamp:
    fontFamily: Inter
    fontSize: 11px
    fontWeight: '400'
    lineHeight: 14px
    letterSpacing: 0.06em
spacing:
  gutter: 1rem
  gutter-tablet: 1.5rem
  gutter-desktop: 2.5rem
  margin: 1.5rem
  margin-tablet: 2.5rem
  margin-desktop: 4rem
  space-xs: 0.25rem
  space-sm: 0.5rem
  space-md: 1rem
  space-lg: 2rem
  space-xl: 3.5rem
---

## Brand & Style
The brand personality is contemplative, archival, and uncompromisingly quiet. Designed specifically for an exhibition of magazine covers, the interface functions like a pristine physical gallery wall: completely silent, allowing the vivid, tactile historical print covers to command full visual dominance. The target audience comprises design historians, typographers, collectors, and editorial purists who appreciate unadorned curation. 

The aesthetic is High-Contrast Editorial Classicism executed through severe minimalism. Physical boxes, borders, cards, shadows, icons, and emojis are entirely eliminated. Visual interest is established strictly through typographic scale, calculated negative space, and pristine cover reproductions. The emotional response is one of reverence, academic clarity, and effortless luxury.

## Colors
The palette is derived from monochromatic gallery spaces. The background canvas is a uniform `#F0F0F0`, providing an unreflective, museum-wall warmth that softens the eye while preserving the original color fidelity of photographed or scanned print media.

- **Primary (`#1A1A1A`)**: Deep soot black, used exclusively for the high-contrast wordmark, primary exhibition titles, and active metadata text.
- **Secondary (`#737373`)**: Muted archival gray for issue dates, volume numbers, running headers, and inactive inputs.
- **Neutral (`#F0F0F0`)**: The continuous background plane. No surface elevation or alternate container fills are permitted; the entire UI shares this singular color floor.
- **Accent/Cover Space**: True color is introduced strictly through the exhibited cover artwork itself.

## Typography
Typography is the primary navigational and brand structure in the absence of UI containers and decorative glyphs.

- **Playfair Display** introduces romantic, high-fashion editorial gravitas. In italicized display scale, it delivers the cursive, timeless signature for the brand mark '1996'. In regular weights, it titles exhibitions, featured publication mastheads, and editorial monographs.
- **Inter** acts as an invisible functional counterweight. Rendered with calibrated letter spacing in small sizes and uppercase treatments, it treats publication dates, photographers, and issue numbers with indexical precision.

## Layout & Spacing
The layout follows a fluid editorial grid designed for full-height visual engagement and rhythm:
- **Mobile**: A single-column or alternating staggered two-column arrangement with `margin: 1.5rem` and `gutter: 1rem`. Covers breathe with deliberate vertical pauses (`space-xl`).
- **Tablet & Large Screens**: An asymmetrical multi-column gallery layout simulating museum vitrines and archival prints, expanding outer margins to `2.5rem`–`4rem`.

Grid elements are anchored by text directly below each cover without bounding boxes. Spacing operates strictly on an 8pt structural rhythm, favoring dramatic vertical separations over compact utility.

## Elevation & Depth
Elevation is strictly planar (zero-elevation). 

- **No Dropshadows**: No drop shadows, ambient halos, or card extrusions are utilized. Depth is communicated strictly through the natural layering of artwork and scale.
- **No Borders or Dividers**: There are no horizontal hairline rules or enclosing strokes. Separation between issues, sections, and structural headers is created solely by negative canvas space (`space-lg` to `space-xl`).
- **Surface Integrity**: All controls, lists, and artworks rest directly upon the unvarnished `#F0F0F0` canvas.

## Shapes
In adherence to classic editorial lithography and the physical trimming of print magazines, the shape language uses pure 90-degree right angles (`roundedness: 0`). 

- Images and magazine covers are rendered with razor-sharp corners (0px radius) to evoke real printed sheets of paper.
- Interactive tap surfaces, search inputs, and highlight states preserve hard, unrounded bounds without rounded pills or rounded corner radii anywhere in the system.

## Components

### Buttons & Interactive Text
- **Action Links**: Text-only actions in `label-caps` or `headline-sm`. No background pills, no container fills, no outlines, and no chevrons or arrow icons.
- **States**: Resting state uses `#1A1A1A` or `#737373`. Tap/Pressed state transitions immediately to `#737373` (or an underline rendered as pure text decoration).

### Search & Filters
- **Minimal Input**: A borderless text prompt on the raw `#F0F0F0` canvas. No magnifying glass icon and no enclosed box. Placeholder text reads "Type an issue, year, or publication..." in `body-lg` using `#737373`.
- **Filtering**: Plain typographic filters laid out in a single horizontal running line with generous spacing (`space-md`). Active filter is rendered in `#1A1A1A` with an underline; inactive options in `#737373`.

### Magazine Exhibition Display (Cards)
- **Cover Display**: Pristine high-resolution cover image with strict zero corner radius.
- **Placard / Metadata**: Positioned directly underneath the cover without an enclosing container or border:
  - Line 1: Month and Year (e.g., "OCTOBER 1996") in `date-stamp` (`#737373`).
  - Line 2: Magazine Title & Edition in `headline-sm` (`#1A1A1A`).
- **Focus**: The image carries 100% of the visual mass; metadata remains discreet.

### Lists & Navigation
- **Index View**: Title and issue year aligned flush left and right across the canvas width. No dividing lines; separation is handled strictly through line-height and vertical whitespace (`space-sm`).
- **Header**: The wordmark '1996' in `display-brand` centered or pinned flush left at top margin, unencumbered by background navigation bars.

### Form & Selection Controls
- **Checkboxes & Radios**: Rendered as typographic selectors (e.g., "[ SELECT ]" / "[ · ]" or simple bold/unbold font weight toggles) without graphic shapes, tick marks, or filled discs.