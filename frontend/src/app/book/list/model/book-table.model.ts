export interface BookTableModel {
  id: number;
  title: string;
  subtitle: string;
  authors: Array<string>;
  categories: Array<string>;
  thumbnail: string;
  description: string;
}
