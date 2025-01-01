# TapTapTap
A fuss-free, command-line tool for quick jot downs.

![Version](https://img.shields.io/badge/version-1.0.0-blue.svg)
![License](https://img.shields.io/badge/license-GPL--3.0-red.svg)

## Features
- Instantly create notes from the command line
- Auto-generated timestamps for note names
- Search through all your notes
- List and view notes with clean formatting
- View note statistics
- Export notes to Downloads folder
- Easy note management and deletion

## Installation

### Prerequisites
- Java Runtime Environment (JRE) 11 or later

### For MacOS and Linux:
1. Download `TapTapTap.jar` and `Install.sh` from the [latest release](https://github.com/yourusername/taptaptap/releases/latest) 
2. Run install script `chmod +x install.sh` then `sudo ./install.sh`
3. That's all, type `ttt` in your terminal to get started.

## Usage

### Creating Notes
```bash
# Quick note (auto-generated name)
ttt content

# Named note
ttt name::content
```

### Viewing and Finding Notes
```bash
# List all notes
ttt -l

# View specific note
ttt -v note

# Search text within notes
ttt -f "text"

# Show statistics
ttt -s
```

### Managing Notes
```bash
# Delete specific note
ttt -d note

# Delete all notes (with confirmation)
ttt -da

# Export note to Downloads
ttt -e note

# Export all notes
ttt -ea
```

## File Structure

- Notes are stored in `~/.taptaptap/`
- Each note is a .txt file with the following format:
  ```
  Note: [note-name]
  Content: [your-note-content]
  ```
- Exports are saved to `~/Downloads/TapTapTap_Exports/`

## Command Reference
```
ttt content                     -> saves note with auto-generated name
ttt name::content               -> saves note with given name
ttt -v name                     -> view specific note
ttt -l                          -> list all notes
ttt -f searchtext               -> search notes
ttt -s                          -> show statistics
ttt -d name                     -> delete note
ttt -da                         -> delete all notes
ttt -e name                     -> export note
ttt -ea                         -> export all notes
```

## License

This project is licensed under the GNU General Public License v3.0 - see the [LICENSE](LICENSE) file for details.
