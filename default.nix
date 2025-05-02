{ pkgs ? import <nixpkgs> {}}:

pkgs.mkShell {
  packages = [ 
    pkgs.google-cloud-sdk
    pkgs.opentofu
  ];
}
