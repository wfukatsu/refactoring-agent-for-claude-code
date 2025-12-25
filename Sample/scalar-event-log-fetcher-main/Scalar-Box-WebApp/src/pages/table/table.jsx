<BootstrapDialog
onClose={handleClose}
aria-labelledby="customized-dialog-title"
open={openDilogBox}
>
<DialogTitle
  sx={{ m: 0, p: 2, fontSize: "25px", fontWeight: "bold" }}
  id="customized-dialog-title"
>
  Add/Edit Role
</DialogTitle>
<IconButton
  aria-label="close"
  onClick={handleClose}
  sx={{
    position: "absolute",
    right: 8,
    top: 8,
    color: (theme) => theme.palette.grey[500],
  }}
>
  <CloseIcon />
</IconButton>
<DialogContent dividers>
  {selectedRowData && (
    <div className="flex flex-col gap-6">
      <TextField
        label="Name"
        value={selectedRowData.name}
        color="secondary"
        focused
      />

      {/* <TextField
        label="Roles"
        value={selectedRowData.roleJson.join(", ")}
        color="secondary"
        focused
      /> */}

      <FormControl sx={{ m: 1, width: 300 }}>
        <InputLabel id="demo-multiple-chip-label">Chip</InputLabel>
        <Select
          labelId="demo-multiple-chip-label"
          id="demo-multiple-chip"
          multiple
          value={selectedRowData.roleJson.join(", ")}
          onChange={handleChange}
          input={
            <OutlinedInput id="select-multiple-chip" label="Chip" />
          }
          renderValue={() => null}
          MenuProps={MenuProps}
        >
          {names.map((name) => (
            <MenuItem
              key={name}
              value={name}
              style={getStyles(name, personName, theme)}
            >
              <Box
                sx={{
                  display: "flex",
                  justifyContent: "space-between",
                  alignItems: "center",
                }}
              >
                <span>{name}</span>{" "}
                <Checkbox checked={personName.indexOf(name) > -1} />
              </Box>
            </MenuItem>
          ))}
        </Select>
      </FormControl>
    </div>
  )}
</DialogContent>
<DialogActions>
  <buttton
    autoFocus
    onClick={handleClose}
    className="bg-[#0061D5] hover:bg-blue-800 text-white font-bold py-2 px-4 rounded-full"
  >
    Apply
  </buttton>
</DialogActions>
</BootstrapDialog>